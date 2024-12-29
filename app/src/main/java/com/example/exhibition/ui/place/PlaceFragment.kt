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
import android.widget.Toast
import java.io.InputStream
import com.example.exhibition.loadJSONFromAsset
import com.example.exhibition.getVenueLocation
import com.example.exhibition.toMutableList
import org.json.JSONArray
import org.json.JSONObject

class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private lateinit var venues: JSONArray
    private var venuesList = mutableListOf<JSONObject>()
    private var isLikeFilterEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val placeViewModel =
            ViewModelProvider(this).get(PlaceViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val jsonString = loadJSONFromAsset(requireContext(), "exhibition_data.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            venues = jsonObject.getJSONArray("venues")
            venuesList = venues.toMutableList()
            val events = jsonObject.getJSONArray("events")

            adapter = PlaceAdapter(
                requireContext(),
                venues,
                events,
                onItemClick = { selectedPlace, exList ->
                    val exList_jsonArray = JSONArray(exList)
                    val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                        putExtra("place_data", selectedPlace.toString())
                        putExtra("ex_list", exList_jsonArray.toString())
                        putExtra("venues", venues.toString())
                    }
                    startActivity(intent)
                },
                onLikeClick = { likedPlace ->
                    toggleLikeState(likedPlace)
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

    private fun toggleLikeState(likedPlace: JSONObject) {
        val index = venuesList.indexOfFirst { it.getInt("venue_id") == likedPlace.getInt("venue_id") }
        if (index != -1) {
            val isLike = likedPlace.getBoolean("isLike")
            venuesList[index].put("isLike", !isLike)
            adapter.notifyItemChanged(index)
        }
    }

    private fun filterLike(): MutableList<JSONObject> {
        return venuesList.filter { it.getBoolean("isLike") }.toMutableList()
    }
}