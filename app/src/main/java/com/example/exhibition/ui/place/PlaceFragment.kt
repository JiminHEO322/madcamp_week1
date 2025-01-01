package com.example.exhibition.ui.place

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exhibition.R
import com.example.exhibition.databinding.FragmentHomeBinding
import com.example.exhibition.toMutableList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import kotlin.math.sqrt

class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // 원본(JSON과 직접 연결되어 변경 사항이 저장되는 곳)
    private var venuesList = mutableListOf<JSONObject>()

    // 화면 표시용(정렬/필터/검색 결과만 반영)
    private var displayVenuesList = mutableListOf<JSONObject>()
    private var currentSearchQuery: String = ""


    private var isLikeFilterEnabled: Boolean = false
    private var isSortedByDistance: Boolean = false
    private var isTextFilterEnabled: Boolean = false

    private val fileName: String = "exhibition_data.json"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermission()

        val placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val jsonString = initializeDefaultJSON(requireContext())
        if (jsonString == null) {
            Toast.makeText(requireContext(), "장소 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return root
        }

        // 1. JSON 파싱
        val jsonObject = JSONObject(jsonString)
        val venues = jsonObject.optJSONArray("venues") ?: JSONArray()
        val events = jsonObject.optJSONArray("events") ?: JSONArray()

        // 2. 원본 데이터 초기화
        venuesList = venues.toMutableList().apply {
            sortBy { it.optString("name") } // 기본: 가나다순
        }
        // 3. 화면 표시용 리스트 초기화
        displayVenuesList = ArrayList(venuesList)

        // 4. 어댑터 설정
        adapter = PlaceAdapter(
            requireContext(),
            JSONArray(displayVenuesList.toString()),
            events,
            onItemClick = { selectedPlace, exList ->
                val exListJsonArray = JSONArray(exList)
                val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                    putExtra("place_data", selectedPlace.toString())
                    putExtra("ex_list", exListJsonArray.toString())
                    putExtra("venues", JSONArray(venuesList.toString()).toString())
                }
                startActivity(intent)
            },
            onLikeClick = { likedPlace ->
                toggleLikeState(likedPlace)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 5. 좋아요 필터
        binding.likeFilterImageView.setOnClickListener { toggleLikeFilter() }
        binding.likeFilterTextView.setOnClickListener { toggleLikeFilter() }

        // 6. 검색 기능
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString() ?: ""
                filterByText(currentSearchQuery)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 7. 정렬 토글 (가나다순 / 거리순)
        binding.sortToggleButton.setOnClickListener { toggleDistanceFilter() }

        // 처음 표시(가나다순 상태)
        updateDisplayListAndAdapter(currentSearchQuery)

        return root
    }

    override fun onResume() {
        super.onResume()
        Log.d("PlaceFragment", "onResume 호출")

        // JSON 다시 로드 (다른 화면에서 수정됐을 수 있으므로)
        val updatedJsonString = loadJSON(requireContext())
        if (updatedJsonString != null) {
            val updatedJsonObject = JSONObject(updatedJsonString)
            val updatedVenues = updatedJsonObject.optJSONArray("venues") ?: JSONArray()

            // venuesList 갱신
            venuesList.clear()
            venuesList.addAll(updatedVenues.toMutableList())
            venuesList.sortBy { it.optString("name") }

            // 표시용 업데이트
            updateDisplayListAndAdapter(currentSearchQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 좋아요 필터 상태를 토글하여 UI 아이콘 및 목록 갱신
     */
    private fun toggleLikeFilter() {
        isLikeFilterEnabled = !isLikeFilterEnabled
        binding.likeFilterImageView.setImageResource(
            if (isLikeFilterEnabled) R.drawable.icon_fulllike else R.drawable.icon_like
        )
        updateDisplayListAndAdapter(currentSearchQuery)
    }

    /**
     * (핵심) 좋아요 토글 시 venuesList(원본)를 먼저 수정하고 JSON에 반영
     * -> 이후 updateDisplayListAndAdapter()를 호출해 displayVenuesList에도 반영
     */
    private fun toggleLikeState(likedPlace: JSONObject) {
        try {
            val venueId = likedPlace.getInt("venue_id")
            // venuesList에서 venue_id 매칭되는 아이템 찾아서 isLike 토글
            val indexInVenuesList = venuesList.indexOfFirst { it.getInt("venue_id") == venueId }
            if (indexInVenuesList != -1) {
                val currentLike = venuesList[indexInVenuesList].getBoolean("isLike")
                venuesList[indexInVenuesList].put("isLike", !currentLike)
                // JSON 파일에 저장
                saveUpdatedJSON()
                // 변경된 내용을 바탕으로 displayVenuesList 재작성
                updateDisplayListAndAdapter(currentSearchQuery)
            }
        } catch (e: Exception) {
            Log.e("PlaceFragment", "toggleLikeState 오류: ${e.message}")
        }
    }

    /**
     * 텍스트 검색 필터
     */
    private fun filterByText(query: String) {
        isTextFilterEnabled = query.isNotEmpty()
        updateDisplayListAndAdapter(query)
    }

    /**
     * 거리순 / 가나다순 정렬 버튼 토글
     */
    private fun toggleDistanceFilter() {
        isSortedByDistance = !isSortedByDistance
        if (!isSortedByDistance) {
            binding.sortToggleButton.text = "가나다순"
        } else {
            binding.sortToggleButton.text = "거리순"
        }
        updateDisplayListAndAdapter(currentSearchQuery)
    }

    /**
     * venuesList를 기반으로 displayVenuesList를 재구성 후 어댑터에 반영
     * @param searchQuery 사용자가 입력한 검색어
     */
    private fun updateDisplayListAndAdapter(searchQuery: String = "") {
        // Step1. venuesList 복사
        val tempList = ArrayList(venuesList)

        // Step2. 검색 필터
        if (searchQuery.isNotEmpty()) {
            val lowerQuery = searchQuery.lowercase()
            tempList.retainAll { venue ->
                val name = venue.optString("name", "").lowercase()
                val address = venue.optString("address", "").lowercase()
                name.contains(lowerQuery) || address.contains(lowerQuery)
            }
        }

        // Step3. 좋아요 필터
        if (isLikeFilterEnabled) {
            tempList.retainAll { it.optBoolean("isLike", false) }
        }

        // Step4. 정렬
        if (isSortedByDistance) {
            // 거리순 정렬
            checkLocationPermission()
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val (latitude, longitude) = location.latitude to location.longitude
                    tempList.sortBy {
                        val lat2 = it.optDouble("latitude", 0.0)
                        val lng2 = it.optDouble("longitude", 0.0)
                        calculateDistance(lat2, lng2, latitude, longitude)
                    }
                    // 정렬 끝난 뒤에 displayVenuesList와 어댑터 갱신
                    displayVenuesList = tempList
                    adapter.updateData(displayVenuesList)
                } else {
                    Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // 가나다순 정렬
            tempList.sortBy { it.optString("name") }
            displayVenuesList = tempList
            adapter.updateData(displayVenuesList)
        }
    }

    /**
     * 두 지점 사이의 간단한 직선 거리 계산(위도, 경도로)
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        return sqrt(dLat * dLat + dLon * dLon)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
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
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    /**
     * 권한이 허용되어 있을 때 마지막 위치를 받아옴
     */
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // 필요하면 location 처리
                }
        }
    }

    /**
     * JSON 파일이 없다면 assets에서 복사, 있으면 로드
     */
    private fun initializeDefaultJSON(context: Context): String? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // assets에서 exhibition_data.json 복사
            try {
                val inputStream: InputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                val defaultJson = String(buffer, Charsets.UTF_8)
                saveJSONToFile(context, defaultJson)
                return defaultJson
            } catch (e: Exception) {
                Log.e("PlaceFragment", "기본 JSON 파일 초기화 오류: ${e.message}")
            }
        }
        return loadJSON(context)
    }

    /**
     * 내부 저장소에서 JSON 파일을 로드
     */
    private fun loadJSON(context: Context): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonData = file.readText()
                Log.d("PlaceFragment", "로드된 JSON 데이터: $jsonData")
                jsonData
            } else {
                Log.w("PlaceFragment", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 로드 중 오류: ${e.message}")
            null
        }
    }

    /**
     * venuesList가 수정된 후 JSON 파일로 저장
     */
    private fun saveUpdatedJSON() {
        try {
            val jsonString = loadJSON(requireContext())
            val jsonObject = if (jsonString != null) {
                JSONObject(jsonString)
            } else {
                // 기본 골격
                JSONObject().apply {
                    put("venues", JSONArray())
                    put("events", JSONArray())
                    put("reviews", JSONArray())
                }
            }
            jsonObject.put("venues", JSONArray(venuesList))
            saveJSONToFile(requireContext(), jsonObject.toString())
            Log.d("PlaceFragment", "JSON 업데이트 완료: ${jsonObject.toString(2)}")
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 업데이트 오류: ${e.message}")
            Toast.makeText(requireContext(), "데이터 저장 중 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * JSON 문자열을 파일로 저장
     */
    private fun saveJSONToFile(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("PlaceFragment", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("PlaceFragment", "JSON 파일 저장 오류: ${e.message}")
        }
    }
}
