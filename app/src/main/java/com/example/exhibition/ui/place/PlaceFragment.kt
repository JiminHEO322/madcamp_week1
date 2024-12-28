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
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher

class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val placeViewModel =
            ViewModelProvider(this).get(PlaceViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textHome
        //placeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        val placeList = listOf(
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "대전 예술의 전당", "대전광역시", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.sample_image, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ")
        )


        val adapter = PlaceAdapter(placeList) { selectedPlace ->
            val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                putExtra("PLACE_TITLE", selectedPlace.title)
                putExtra("PLACE_ADDRESS", selectedPlace.addressAll)
                putExtra("PLACE_PHONE", selectedPlace.phone)
                putExtra("PLACE_EXNUM", selectedPlace.exNum)
                putExtra("PLACE_IMAGE", selectedPlace.imageResId)
                putExtra("PLACE_URL", selectedPlace.url)
                putExtra("PLACE_INSTAGRAM", selectedPlace.instagram)
                putExtra("PLACE_YOUTUBE", selectedPlace.youtube)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}