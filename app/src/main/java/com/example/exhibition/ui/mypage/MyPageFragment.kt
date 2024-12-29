package com.example.exhibition.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.exhibition.databinding.FragmentMypageBinding
import android.content.Intent
import android.widget.Toast
import com.example.exhibition.loadJSONFromAsset
import org.json.JSONObject
import com.example.exhibition.toMutableList


class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
//        val recyclerView: RecyclerView = view.findViewById(R.id.review_recyclerView)

        val jsonString = loadJSONFromAsset(requireContext(), "exhibition_data.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val reviews = jsonObject.getJSONArray("reviews").toMutableList()

            val adapter = MyPageAdapter(requireContext(), reviews) { selectedReview ->
                val intent = Intent(requireContext(), ReviewDetailActivity::class.java).apply {
                    putExtra("review_data", selectedReview.toString())
                }
                startActivity(intent)
            }

            binding.reviewRecyclerView.layoutManager = GridLayoutManager(context, 3)
            binding.reviewRecyclerView.adapter = adapter
        } else{
            Toast.makeText(requireContext(), "리뷰가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}