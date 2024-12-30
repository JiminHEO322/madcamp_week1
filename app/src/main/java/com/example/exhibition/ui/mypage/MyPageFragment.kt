package com.example.exhibition.ui.mypage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.exhibition.databinding.FragmentMypageBinding
import android.content.Intent
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import com.example.exhibition.toMutableList
import org.json.JSONArray
import java.io.File
import java.io.InputStream


class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val fileName: String = "exhibition_data.json"
    private var reviews = mutableListOf<JSONObject>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
//        val recyclerView: RecyclerView = view.findViewById(R.id.review_recyclerView)

        val jsonString = initializeDefaultJSON(requireContext())

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            reviews = jsonObject.getJSONArray("reviews").toMutableList()
            Log.d("MyPageFragment", "reviews: $reviews")

            val adapter = MyPageAdapter(requireContext(), reviews) { selectedReview ->
                val intent = Intent(requireContext(), ReviewDetailActivity::class.java).apply {
                    Log.d("MyPageFragment", "selectedReview: ${selectedReview.toString()}")
                    val reviewId = selectedReview.getInt("review_id")
                    Log.d("MyPageFragment", "reviewId: $reviewId")
                    putExtra("review_id", reviewId)
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