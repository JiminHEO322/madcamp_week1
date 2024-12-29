package com.example.exhibition.ui.event

//import EventDetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.databinding.FragmentEventBinding
import com.example.exhibition.R
import android.content.Intent
import org.json.JSONObject
import android.content.Context
import org.json.JSONArray
import java.io.InputStream


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

            val adapter = EventAdapter(requireContext(), venues, events) { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                    putExtra("event_data", selectedEvent.toString())
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }

            // RecyclerView 설정
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recyclerView.adapter = adapter
        } else {
            Toast.makeText(requireContext(), "JSON 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getVenueLocation(venues: JSONArray, venueID: Int): String? {
        for (i in 0 until venues.length()){
            val venue = venues.getJSONObject(i)
            if (venue.getInt("venue_id") == venueID) {
                return venue.getString("name")
            }
        }
        return "-"
    }
}
