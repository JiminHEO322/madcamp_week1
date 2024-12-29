package com.example.exhibition.ui.event

//import EventDetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exhibition.databinding.FragmentEventBinding
import android.content.Intent
import org.json.JSONObject
import com.example.exhibition.loadJSONFromAsset
import com.example.exhibition.getVenueLocation


class EventFragment : Fragment() {
    private var _binding: FragmentEventBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
//        val view = inflater.inflate(R.layout.fragment_event, container, false)
//        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val jsonString = loadJSONFromAsset(requireContext(), "exhibition_data.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val venues = jsonObject.getJSONArray("venues")
            val events = jsonObject.getJSONArray("events")

            val topEvents = mutableListOf<JSONObject>()
            val bottomEvents = mutableListOf<JSONObject>()
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                when (event.getString("category")) {
                    "전시" -> topEvents.add(event)
                    "공연" -> bottomEvents.add(event)
                }
            }

            val topAdapter = EventAdapter(requireContext(), venues, topEvents) { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                    putExtra("event_data", selectedEvent.toString())
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }

            val bottomAdapter = EventAdapter(requireContext(), venues, bottomEvents) { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                    putExtra("event_data", selectedEvent.toString())
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }

            // RecyclerView 설정
            binding.recyclerViewTop.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewTop.adapter = topAdapter

            binding.recyclerViewBottom.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewBottom.adapter = bottomAdapter
        } else {
            Toast.makeText(requireContext(), "공연 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
