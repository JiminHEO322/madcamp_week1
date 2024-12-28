package com.example.exhibition.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.exhibition.databinding.FragmentMypageBinding
import com.example.exhibition.R
import android.content.Intent
import com.example.exhibition.model.Review


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
        val root: View = binding.root
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.review_recyclerView)

        val reviews = listOf(
            Review(R.drawable.photo1, "우연히 웨스 앤더슨 2", "2024.10.20", "재밌다"),
            Review(R.drawable.photo2, "뮤지컬 지킬앤하이드", "2024.10.14", "재"),
            Review(R.drawable.photo3, "피아노 파 드 되", "2024.10.20", "밌"),
            Review(R.drawable.photo1, "우연히 웨스 앤더슨 2", "2024.10.20", "다"),
            Review(R.drawable.photo2, "뮤지컬 지킬앤하이드", "2024.10.14", "재"),
            Review(R.drawable.photo3, "피아노 파 드 되", "2024.10.20", "밌"),
            Review(R.drawable.photo1, "우연히 웨스 앤더슨 2", "2024.10.20", "다")
        )

        val adapter = MyPageAdapter(reviews) { selectedReview ->
            val intent = Intent(requireContext(), ReviewDetailActivity::class.java).apply {
                putExtra("REVIEW_IMAGE", selectedReview.imageResId)
                putExtra("REVIEW_TITLE", selectedReview.title)
            }
            startActivity(intent)
        }

        binding.reviewRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.reviewRecyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}