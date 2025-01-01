package com.example.exhibition.ui.event

//import EventDetailFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exhibition.databinding.FragmentEventBinding
import android.content.Intent
import android.util.Log
import org.json.JSONObject
import com.example.exhibition.getVenueLocation
import org.json.JSONArray
import java.io.File
import java.io.InputStream
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.exhibition.EqualSpacingItemDecoration


class EventFragment : Fragment() {
    private var _binding: FragmentEventBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var venues: JSONArray
    private lateinit var events: JSONArray
    private val fileName: String = "exhibition_data.json"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
//        val view = inflater.inflate(R.layout.fragment_event, container, false)
//        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val filePath = File(requireContext().filesDir, fileName)
        if (!filePath.exists()) {
            Log.e("EventFragment", "JSON 파일 경로가 잘못되었습니다: ${filePath.absolutePath}")
        } else{
            Log.d("EventFragment", "올바른 파일 경로")
        }

        val jsonString = initializeDefaultJSON(requireContext())

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            venues = jsonObject.getJSONArray("venues")
            events = jsonObject.getJSONArray("events")

            val topEvents = mutableListOf<JSONObject>()
            val bottomEvents = mutableListOf<JSONObject>()
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                when (event.getString("category")) {
                    "전시" -> topEvents.add(event)
                    "공연" -> bottomEvents.add(event)
                }
            }

            val topAdapter = EventAdapter(requireContext(), venues, topEvents, { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                    putExtra("event_id", selectedEvent.getString("event_id"))
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }, isFullWidth = false) // match_parent가 아닌 기본 크기 (300dp)

            val bottomAdapter = EventAdapter(requireContext(), venues, bottomEvents, { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                    putExtra("event_id", selectedEvent.getString("event_id"))
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }, isFullWidth = false) // match_parent가 아닌 기본 크기 (300dp)

            // RecyclerView 설정
            binding.recyclerViewTop.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = topAdapter

                addItemDecoration(EqualSpacingItemDecoration(125, 16))
            }

            binding.recyclerViewBottom.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = bottomAdapter

                addItemDecoration(EqualSpacingItemDecoration(125, 16))
            }
        } else {
            Toast.makeText(requireContext(), "공연 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initializeDefaultJSON(context: Context): String? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            try {
                val inputStream: InputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                val defaultJson = String(buffer, Charsets.UTF_8)
                saveJSONToFile(context, defaultJson) // 파일 저장
                Log.d("PlaceFragment", "기본 JSON 파일 생성 완료")
                return defaultJson
            } catch (e: Exception) {
                Log.e("PlaceFragment", "기본 JSON 파일 초기화 중 오류 발생: ${e.message}")
            }
        }
        return loadJSON(context) // 파일이 있으면 로드
    }

    private fun loadJSON(context: Context): String? {
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

    private fun saveJSONToFile(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("EventFragment", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("EventFragment", "JSON 파일 저장 중 오류 발생: ${e.message}")
        }
    }

}
