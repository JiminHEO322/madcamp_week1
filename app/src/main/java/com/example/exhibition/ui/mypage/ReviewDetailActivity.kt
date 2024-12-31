package com.example.exhibition.ui.mypage

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import com.example.exhibition.model.Review
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream


class ReviewDetailActivity : AppCompatActivity() {

    private lateinit var reviews: JSONArray
    private lateinit var events: JSONArray

    private lateinit var date_textView: TextView
    private lateinit var date_editText: EditText
    private lateinit var content_textView: TextView
    private lateinit var content_editText: EditText

    private var reviewId : Int = 0
    private var isEditing: Boolean = false

    private val fileName: String = "exhibition_data.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val jsonString = initializeDefaultJSON(this)

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            events = jsonObject.getJSONArray("events")
            reviews = jsonObject.getJSONArray("reviews")

            reviewId = intent.getIntExtra("review_id", 0)
            Log.d("ReviewDetailActivity", "받은 reviewIdString: $reviewId")

            val review = findReview(reviewId)
            val event = findEvent(reviewId)

            val imageName = review.optString("image", "photo1")
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

            findViewById<ImageView>(R.id.detailReviewImage).setImageResource(imageResId)
            findViewById<TextView>(R.id.detailReviewTitle).text = event.getString("title")

            // 뷰 초기화
            date_textView = findViewById(R.id.detailReviewDate)
            date_editText = findViewById(R.id.editDate)
            content_textView = findViewById(R.id.detailReviewContent)
            content_editText = findViewById(R.id.editContent)

            date_editText.visibility = android.view.View.GONE
            content_editText.visibility = android.view.View.GONE

            date_textView.text = review.getString("date")
            content_textView.text = review.getString("content")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }

    // 메뉴를 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_review, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_edit)?.isVisible = !isEditing
        menu?.findItem(R.id.action_save)?.isVisible = isEditing
        return super.onPrepareOptionsMenu(menu)
    }

    // 메뉴 클릭 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                // 수정 모드
                isEditing = true
                toggleEditMode(isEditing)
                invalidateOptionsMenu()
                return true
            }

            R.id.action_save -> {
                // 저장 모드
                isEditing = false
                saveText()
                toggleEditMode(isEditing)
                invalidateOptionsMenu()
                return true
            }

            R.id.action_delete -> {
                deleteReview()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReview() {
        Log.d("ReviewDetailActivity", "delete")
        AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("이 리뷰를 정말로 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                try {
                    // 리뷰 삭제
                    for (i in 0 until reviews.length()) {
                        val review = reviews.getJSONObject(i)
                        if (review.getInt("review_id") == reviewId) {
                            reviews.remove(i) // 리뷰 삭제

                            for (j in 0 until events.length()){
                                val event = events.getJSONObject(i)
                                if (event.getInt("event_id") == reviewId) {
                                    event.put("viewed", false)
                                    break
                                }
                            }
                            Log.d("ReviewDetailAcitivty", "리뷰 삭제 완료")
                            saveUpdatedJSON() // JSON 파일 업데이트

                            Toast.makeText(this, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            finish() // 액티비티 종료
                            return@setPositiveButton
                        }
                    }
                    Toast.makeText(this, "리뷰를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("ReviewDetailActivity", "리뷰 삭제 중 오류 발생: ${e.message}")
                    Toast.makeText(this, "삭제 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss() // 팝업 닫기
            }
            .show()
    }


    private fun toggleEditMode(enable: Boolean) {
        if (enable) {
            date_editText.visibility = android.view.View.VISIBLE
            date_textView.visibility = android.view.View.GONE
            date_editText.setText(date_textView.text) // 기존 텍스트를 EditText로 설정

            content_editText.visibility = android.view.View.VISIBLE
            content_textView.visibility = android.view.View.GONE
            content_editText.setText(content_textView.text) // 기존 텍스트를 EditText로 설정
        } else {
            date_editText.visibility = android.view.View.GONE
            date_textView.visibility = android.view.View.VISIBLE

            content_editText.visibility = android.view.View.GONE
            content_textView.visibility = android.view.View.VISIBLE
        }
    }

    private fun saveText() {
        // 수정된 텍스트를 TextView에 반영
        date_textView.text = date_editText.text.toString()
        content_textView.text = content_editText.text.toString()

        // 수정된 텍스트를 JSON 파일에 반영
        for (i in 0 until reviews.length()) {
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId) {
                val date = date_editText.text.toString()
                val content = content_editText.text.toString()

                review.put("date", date)
                review.put("content", content)

                saveUpdatedJSON()
                Log.d("EventFragment", "이벤트 상태 변경 및 저장 완료: ${review.toString(2)}")

                break
            }
        }
    }

    private fun findReview(reviewId: Int): JSONObject {
        for (i in 0 until reviews.length()){
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId){
                return review
            }
        }
        return JSONObject()
    }

    private fun findEvent(eventId: Int): JSONObject {
        for (i in 0 until events.length()){
            val event = events.getJSONObject(i)
            if (event.getInt("event_id") == eventId){
                return event
            }
        }
        return JSONObject()
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
                Log.d("ReviewDetailActivity", "로드된 JSON 데이터: $jsonData") // 확인용 로그
                jsonData
            } else {
                Log.w("ReviewDetailActivity", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 로드 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun saveUpdatedJSON() {
        try {
            // 기존 JSON 데이터 읽기
            val jsonString = loadJSON(this)
            val jsonObject = if (jsonString != null) {
                JSONObject(jsonString)
            } else {
                JSONObject().apply {
                    put("venues", JSONArray())
                    put("events", JSONArray())
                    put("reviews", JSONArray())
                }
            }

            jsonObject.put("events", events)
            jsonObject.put("reviews", reviews)

            // 파일에 저장
            saveJSONToFile(this, jsonObject.toString())
            Log.d("ReviewDetailActivity", "JSON 업데이트 완료: ${jsonObject.toString(2)}") // 확인용 로그
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 업데이트 중 오류 발생: ${e.message}")
            Toast.makeText(this, "데이터 저장 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveJSONToFile(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("ReviewDetailActivity", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 파일 저장 중 오류 발생: ${e.message}")
        }
    }
}