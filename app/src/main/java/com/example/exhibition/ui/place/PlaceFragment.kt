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
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast

class PlaceFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private lateinit var placeList: MutableList<Place>
    private lateinit var searchEditText: EditText
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

        //val textView: TextView = binding.textHome
        //placeViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        placeList = mutableListOf(
            Place(R.drawable.yesul, "예술의 전당", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.joymaru, "조이마루아트홀", "대전광역시 유성구", "12345678", "112", "대전광역시 유성구 엑스포로97번길",
                "https://plana2021.modoo.at/", "https://www.instagram.com/plana20210301/", "null"),
            Place(R.drawable.bluesquare, "블루스퀘어", "서울특별시 용산구", "12345678", "112", "서울특별시 용산구 이태원로 294",
                "https://m.bluesquare.kr/", "null", "null"),
            Place(R.drawable.yesul, "abc", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.yesul, "cde", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.yesul, "efg", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.yesul, "ghi", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ"),
            Place(R.drawable.yesul, "ijk", "서울특별시 서초구", "12345678", "112", "서울특별시 서초구 남부순환로 2406",
                "https://www.sac.or.kr/", "https://www.instagram.com/seoul_arts_center", "https://www.youtube.com/channel/UCUn8h6aG6rCM1zvXKIuNpVQ")
        )

        adapter = PlaceAdapter(
            placeList,
            onItemClick = { selectedPlace ->
            val intent = Intent(requireContext(), PlaceDetailActivity::class.java).apply {
                putExtra("PLACE_TITLE", selectedPlace.title)
                putExtra("PLACE_ADDRESS", selectedPlace.addressAll)
                putExtra("PLACE_PHONE", selectedPlace.phone)
                putExtra("PLACE_EXNUM", selectedPlace.exNum)
                putExtra("PLACE_IMAGE", selectedPlace.imageResId)
                putExtra("PLACE_URL", selectedPlace.url)
                putExtra("PLACE_INSTAGRAM", selectedPlace.instagram)
                putExtra("PLACE_YOUTUBE", selectedPlace.youtube)
                putExtra("PLACE_ISLIKE", selectedPlace.isLike)
            }
            startActivity(intent)
        },
            onLikeClick = { likedPlace ->
                val index = placeList.indexOf(likedPlace)
                if (index != -1) {
                    placeList[index].isLike = !likedPlace.isLike
                    adapter.notifyItemChanged(index) // UI 갱신
                }
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.likeFilterImageView.setOnClickListener {
            isLikeFilterEnabled = !isLikeFilterEnabled // 필터 상태 반전
            if (isLikeFilterEnabled) {
                binding.likeFilterImageView.setImageResource(R.drawable.icon_fulllike) // 필터 활성화 아이콘
                val filteredList = placeList.filter { it.isLike } // 좋아요 데이터만 필터링
                adapter.updateData(filteredList)
            } else {
                binding.likeFilterImageView.setImageResource(R.drawable.icon_like) // 필터 비활성화 아이콘
                adapter.updateData(placeList) // 전체 데이터 표시
            }
        }
        binding.likeFilterTextView.setOnClickListener {
            isLikeFilterEnabled = !isLikeFilterEnabled // 필터 상태 반전
            if (isLikeFilterEnabled) {
                binding.likeFilterImageView.setImageResource(R.drawable.icon_fulllike) // 필터 활성화 아이콘
                val filteredList = placeList.filter { it.isLike } // 좋아요 데이터만 필터링
                adapter.updateData(filteredList)
            } else {
                binding.likeFilterImageView.setImageResource(R.drawable.icon_like) // 필터 비활성화 아이콘
                adapter.updateData(placeList) // 전체 데이터 표시
            }
        }

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