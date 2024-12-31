package com.example.exhibition.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.exhibition.databinding.FragmentHomeBinding
import com.example.exhibition.model.Place
import com.example.exhibition.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import java.io.InputStream
import com.example.exhibition.toMutableList
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private lateinit var venues: JSONArray
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var venuesList = mutableListOf<JSONObject>()
    private var isLikeFilterEnabled: Boolean = false
    private var isSortedByDistance: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermission()

        val placeViewModel =
            ViewModelProvider(this).get(PlaceViewModel::class.java)
//        val placeViewModel =
//            ViewModelProvider(this).get(PlaceViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val fileName: String = "exhibition_data.json"

        val filePath = File(requireContext().filesDir, fileName)
        if (!filePath.exists()) {
            Log.e("PlaceFragment", "JSON 파일 경로가 잘못되었습니다: ${filePath.absolutePath}")
        } else{
            Log.d("PlaceFragment", "올바른 파일 경로")
        }

        val jsonString = initializeDefaultJSON(requireContext(), fileName)
//        val jsonString = loadJSON(requireContext(), "exhibition_data.json")

        if (jsonString == null){
            Log.e("PlaceFragment", "JSON 파일을 찾을 수 없습니다")
        }
        if (jsonString != null) {
            Log.d("PlaceFragment", "JSON 데이터 로드 성공: $jsonString")

            val jsonObject = JSONObject(jsonString)
            venues = jsonObject.getJSONArray("venues") ?: JSONArray()
            venuesList = venues.toMutableList()
            venuesList.sortBy { it.getString("name")}



            val events = jsonObject.getJSONArray("events")?: JSONArray()

            adapter = PlaceAdapter(
                requireContext(),
                venues,
                events,
                onItemClick = { selectedPlace, exList ->
                    val exListJsonArray = JSONArray(exList)
                    val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                        putExtra("place_data", selectedPlace.toString())
                        putExtra("ex_list", exListJsonArray.toString())
                        putExtra("venues", venues.toString())
                    }
                    startActivity(intent)
                },
                onLikeClick = { likedPlace ->
                    toggleLikeState(likedPlace, jsonObject)
                    Log.d("PlaceFragment", "isLike JSON 파일 업데이트 완료: $jsonObject.toString()")
                    val editedJsonString = initializeDefaultJSON(requireContext(), "exhibition_data.json")
                    Log.d("PlaceFragment", "edited JSON : $editedJsonString")
//                    saveJSONToFile(requireContext(), "exhibition_data.json", jsonObject.put("venues", JSONArray(venuesList)).toString())
                }
            )

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter

            binding.likeFilterImageView.setOnClickListener { toggleLikeFilter() }
            binding.likeFilterTextView.setOnClickListener { toggleLikeFilter() }

            binding.searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 입력 전의 상태
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 입력 중의 상태
                    adapter.filter(s.toString()) // 검색 기능 호출
                }
                override fun afterTextChanged(s: Editable?) {
                    // 입력 후의 상태
                }
            })
            binding.sortToggleButton.setOnClickListener { toggleDistanceFilter() }
            adapter.updateData(venuesList) // 초기에 가나다순으로 정렬


        }
        else {
            Toast.makeText(requireContext(), "장소 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleLikeFilter() {
        isLikeFilterEnabled = !isLikeFilterEnabled
        binding.likeFilterImageView.setImageResource(
            if (isLikeFilterEnabled) R.drawable.icon_fulllike else R.drawable.icon_like
        )
        val filteredList = if (isLikeFilterEnabled) filterLike() else venuesList
        adapter.updateData(filteredList)
    }

    private fun toggleLikeState(likedPlace: JSONObject, jsonObject: JSONObject) {
        Log.d("PlaceFragment", "likedplace: $likedPlace")
        for (i in 0 until venues.length()) {
            val venue = venues.getJSONObject(i)
            if (venue.getInt("venue_id") == likedPlace.get("venue_id")) {
                val currentLikeState = venue.getBoolean("isLike")
                venue.put("isLike", !currentLikeState)

                saveJSONToFile(requireContext(), "exhibition_data.json", jsonObject.toString())
                Log.d("PlaceFragment", "isLike 상태 변경 완료: ${venue.toString(2)}")
                val savedFile = loadJSON(requireContext(), "exhibition_data.json")
                Log.d("PlaceFragment", "savedFile : $savedFile")

                adapter.notifyItemChanged(i)

                saveUpdatedJSON()
            }
        }
    }

    private fun filterLike(): MutableList<JSONObject> {
        return venuesList.filter { it.getBoolean("isLike") }.toMutableList()
    }

    private fun toggleDistanceFilter() {
        isSortedByDistance = !isSortedByDistance
        if (!isSortedByDistance) {
            // 가나다순 정렬
            venuesList.sortBy { it.getString("name")}
            adapter.updateData(venuesList)
            binding.sortToggleButton.text = "가나다순"
        } else {
            checkLocationPermission()
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    venuesList.sortBy {
                        val itsLatitude = it.optDouble("latitude", 0.0)
                        val itsLongitude = it.optDouble("longitude", 0.0)
                        val distance = calculateDistance(itsLatitude, itsLongitude, latitude, longitude)
                        distance
                    }
                    adapter.updateData(venuesList)
                    binding.sortToggleButton.text = "거리순"
                } else {
                    Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1)
        val dLon = (lon2 - lon1)
        return Math.sqrt(Math.pow(dLat,2.0) + Math.pow(dLon,2.0))
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 이미 허용된 경우
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation() // 권한 승인 후 위치 가져오기
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // 위치 데이터 활용
                } else {
                    Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initializeDefaultJSON(context: Context, fileName: String): String? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            try {
                val inputStream: InputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                val defaultJson = String(buffer, Charsets.UTF_8)
                saveJSONToFile(context, fileName, defaultJson) // 파일 저장
                Log.d("PlaceFragment", "기본 JSON 파일 생성 완료")
                return defaultJson
            } catch (e: Exception) {
                Log.e("PlaceFragment", "기본 JSON 파일 초기화 중 오류 발생: ${e.message}")
            }
        }
        return loadJSON(context, fileName) // 파일이 있으면 로드
//        val inputStream: InputStream = context.assets.open(fileName)
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer)
//        inputStream.close()
//
//        val defaultJson = String(buffer, Charsets.UTF_8)
//        saveJSONToFile(context, fileName, defaultJson) // 파일 저장
//        Log.d("PlaceFragment", "기본 JSON 파일 생성 완료")
//        return defaultJson
    }

    private fun loadJSON(context: Context, fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonData = file.readText()
                Log.d("PlaceFragment", "로드된 JSON 데이터: $jsonData") // 확인용 로그
                jsonData
            } else {
                Log.w("PlaceFragment", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 로드 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun saveUpdatedJSON() {
        try {
            // 기존 JSON 데이터 읽기
            val jsonString = loadJSON(requireContext(), "exhibition_data.json")
            val jsonObject = if (jsonString != null) {
                JSONObject(jsonString)
            } else {
                JSONObject().apply {
                    put("venues", JSONArray())
                    put("events", JSONArray())
                    put("reviews", JSONArray())
                }
            }

            // venues 업데이트
            jsonObject.put("venues", JSONArray(venuesList))

            // 파일에 저장
            saveJSONToFile(requireContext(), "exhibition_data.json", jsonObject.toString())
            Log.d("PlaceFragment", "JSON 업데이트 완료: ${jsonObject.toString(2)}") // 확인용 로그
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 업데이트 중 오류 발생: ${e.message}")
            Toast.makeText(requireContext(), "데이터 저장 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveJSONToFile(context: Context, fileName: String, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("PlaceFragment", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 파일 저장 중 오류 발생: ${e.message}")
        }
    }
}