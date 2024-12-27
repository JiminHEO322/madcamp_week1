package com.example.exhibition.ui.place

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
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val placeViewModel =
            ViewModelProvider(this).get(PlaceViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        placeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val placeList = listOf(
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "1668-1352", "112", "서울특별시 서초구 남부순환로 2406"),
            Place(R.drawable.sample_image, "전시장 B", "서울특별시 강남구", "1234-5678", "진행중", "119"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110"),
            Place(R.drawable.sample_image, "전시장 C", "서울특별시 송파구", "8765-4321", "진행중", "110")
        )


        val adapter = PlaceAdapter(placeList) { selectedPlace ->
            val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                putExtra("PLACE_TITLE", selectedPlace.title)
                putExtra("PLACE_ADDRESS", selectedPlace.addressAll)
                putExtra("PLACE_PHONE", selectedPlace.phone)
                putExtra("PLACE_EXNUM", selectedPlace.exNum)
                putExtra("PLACE_IMAGE", selectedPlace.imageResId)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}