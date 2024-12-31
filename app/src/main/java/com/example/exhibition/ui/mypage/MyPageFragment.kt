package com.example.exhibition.ui.mypage

import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import org.json.JSONObject
import com.example.exhibition.toMutableList
import com.example.exhibition.ui.place.PlaceAdapter
import org.json.JSONArray
import java.io.File
import java.io.InputStream


class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: MyPageAdapter

    private val fileName: String = "exhibition_data.json"
    private var events = mutableListOf<JSONObject>()
    private var reviews = mutableListOf<JSONObject>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // json 데이터 불러오기
        val jsonString = initializeDefaultJSON(requireContext())

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            events = jsonObject.getJSONArray("events").toMutableList()
            reviews = jsonObject.getJSONArray("reviews").toMutableList()
            Log.d("MyPageFragment", "reviews: $reviews")

            val performanceNumber = getPerformanceNumber()
            val exhibitionNumber = reviews.size - performanceNumber

            binding.performanceNumberText.text = "$performanceNumber"
            binding.exhibitionNumberText.text = "$exhibitionNumber"

            adapter = MyPageAdapter(requireContext(), reviews) { selectedReview ->
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

            adapter.notifyDataSetChanged()
        } else{
            Toast.makeText(requireContext(), "리뷰가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.d("MyPageFragment", "RESUME")

        // JSON 파일에서 데이터를 다시 로드
        val updatedJsonString = loadJSON(requireContext())
        if (updatedJsonString != null) {
            val updatedJsonObject = JSONObject(updatedJsonString)
            reviews.clear() // 기존 데이터를 삭제
            reviews.addAll(updatedJsonObject.getJSONArray("reviews").toMutableList())

            val performanceNumber = getPerformanceNumber()
            val exhibitionNumber = reviews.size - performanceNumber

            binding.performanceNumberText.text = "$performanceNumber"
            binding.exhibitionNumberText.text = "$exhibitionNumber"

            // 어댑터에 데이터 갱신 알림
            adapter.notifyDataSetChanged()
            Log.d("MyPageFragment", "onResume: 어댑터 데이터 업데이트 완료")
        } else {
            Log.w("MyPageFragment", "onResume: JSON 데이터를 다시 로드할 수 없습니다.")
        }
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
                Log.d("MyPageFragment", "기본 JSON 파일 생성 완료")
                return defaultJson
            } catch (e: Exception) {
                Log.e("MyPageFragment", "기본 JSON 파일 초기화 중 오류 발생: ${e.message}")
            }
        }
        return loadJSON(context) // 파일이 있으면 로드
    }

    private fun loadJSON(context: Context): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonData = file.readText()
                Log.d("MyPageFragment", "로드된 JSON 데이터: $jsonData") // 확인용 로그
                jsonData
            } else {
                Log.w("MyPageFragment", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("MyPageFragment", "JSON 로드 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun saveJSONToFile(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("MyPageFragment", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("MyPageFragment", "JSON 파일 저장 중 오류 발생: ${e.message}")
        }
    }

    private fun getPerformanceNumber(): Int {
        var num = 0
        // 이벤트 데이터를 맵으로 변환하여 검색 속도 개선
        val eventMap = events.associateBy { it.getInt("event_id") }

        for (review in reviews) {
            val reviewId = review.getInt("review_id")
            val event = eventMap[reviewId]
            if (event != null && event.getString("category") == "공연") {
                num++
            }
        }

        return num
    }
}