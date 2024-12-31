package com.example.exhibition.ui.event

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.exhibition.toMutableList
import com.example.exhibition.ui.mypage.ReviewDetailActivity
import java.io.File
import java.io.InputStream
import android.widget.Button
import android.net.Uri


class EventDetailActivity : AppCompatActivity() {

    private lateinit var saveImageView: ImageView
    private var isViewed: Boolean = false

    private lateinit var events: JSONArray
    private lateinit var reviews: JSONArray
    private var eventId: Int = 0
    private val fileName: String = "exhibition_data.json"
    private lateinit var openUrlButton: Button
    private var eventUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "상세 정보"
        }

        updateView()
    }

    override fun onResume() {
        super.onResume()
        Log.d("EventDetailActivty", "RESUME")

        setContentView(R.layout.activity_event_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "상세 정보"
        }

        updateView()
    }

    private fun updateView(){
        val jsonString = initializeDefaultJSON(this)
        val jsonObject = JSONObject(jsonString)
        events = jsonObject.getJSONArray("events")
        reviews = jsonObject.getJSONArray("reviews")

        eventId = intent.getIntExtra("event_id", 0)
        val location = intent.getStringExtra("event_location")
        Log.d("EventDetailActivity", "received event id : $eventId")

        if (eventId != 0) {
            val eventJsonObject = findJsonObject(eventId)
            val title = eventJsonObject.getString("title")
            val date = eventJsonObject.getString("date")
            val imageName = eventJsonObject.getString("image")
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
            val actor = eventJsonObject.getString("actor")
            eventUrl = eventJsonObject.getString("url")

            isViewed = eventJsonObject.getBoolean("viewed")
            saveImageView = findViewById<ImageView>(R.id.saveButton)

            findViewById<ImageView>(R.id.detailEventImage).setImageResource(imageResId)
            findViewById<TextView>(R.id.detailEventTitle).text = title
            findViewById<TextView>(R.id.detailEventLocation).text = location
            findViewById<TextView>(R.id.detailEventDate).text = date
            findViewById<TextView>(R.id.detailEventActor).text = actor

            openUrlButton = findViewById(R.id.openUrlButton)
            openUrlButton.setOnClickListener {
                openEventUrl()
            }

            Log.d("EventDetailActivity", "eventId: $eventId,  isViewed: $isViewed")
            saveImageView.setImageResource(
                if (isViewed) R.drawable.review_on else R.drawable.review_off
            )
            Log.d("EventDetailActivity", "isViewed: $isViewed")


            saveImageView.setOnClickListener{
                if (isViewed) {
                    val eventIntent = Intent(this, ReviewDetailActivity::class.java).apply{
                        putExtra("review_id", eventId)
                    }
                    startActivity(eventIntent)
                } else {
                    toggleState(eventId)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }

    private fun toggleState(eventId: Int){
        for (i in 0 until events.length()) {
            val event = events.getJSONObject(i)
            if (event.getInt("event_id") == eventId) {
                val currentViewedState = event.getBoolean("viewed")
                val newViewedState = !currentViewedState
                event.put("viewed", newViewedState)

                saveUpdatedJSON()
                Log.d("EventFragment", "이벤트 상태 변경 및 저장 완료: ${event.toString(2)}")

                isViewed = newViewedState
                saveImageView.setImageResource(
                    if (isViewed) R.drawable.review_on else R.drawable.review_off
                )
                Log.d("EventDetailActivity", "UI 업데이트 완료: isViewed=$isViewed")

                if (isViewed){
                    addReview(event.getInt("event_id"))
                    saveUpdatedJSON()
                    Log.d("EventFragment", "리뷰 추가: ${reviews.toString(2)}")

                }
                break
            }
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
                Log.d("EventFragment", "기본 JSON 파일 생성 완료")
                return defaultJson
            } catch (e: Exception) {
                Log.e("EventFragment", "기본 JSON 파일 초기화 중 오류 발생: ${e.message}")
            }
        }
        return loadJSON(context) // 파일이 있으면 로드
    }

    private fun loadJSON(context: Context): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonData = file.readText()
                Log.d("EventFragment", "로드된 JSON 데이터: $jsonData") // 확인용 로그
                jsonData
            } else {
                Log.w("EventFragment", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("EventFragment", "JSON 로드 중 오류 발생: ${e.message}")
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
            Log.d("EventFragment", "JSON 업데이트 완료: ${jsonObject.toString(2)}") // 확인용 로그
        } catch (e: Exception) {
            Log.e("EventFragment", "JSON 업데이트 중 오류 발생: ${e.message}")
            Toast.makeText(this, "데이터 저장 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun findJsonObject(eventId: Int): JSONObject {
        for (i in 0 until events.length()) {
            val event = events.getJSONObject(i)
            if (event.getInt("event_id") == eventId) {
                return event
            }
        }
        return JSONObject()
    }

    private fun addReview(eventId: Int){
        for (i in 0 until reviews.length()){
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == eventId) {
                Log.w("EventDetailActivity", "이미 리뷰가 존재합니다: eventId=$eventId")
                Toast.makeText(this, "이미 관람한 전시입니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val defaultReviewImage = findImage(eventId)

        val newReview = JSONObject().apply {
            put("review_id", eventId)   // review_id는 event_id와 같도록 설정
            put("image", defaultReviewImage)
            put("date", "") // 현재 날짜를 가져오는 함수 호출
            put("summary", "") // 한줄평
            put("content", "") // 리뷰 내용
        }
        reviews.put(newReview) // reviews 배열에 추가

        Toast.makeText(this, "My Page에 추가되었습니다", Toast.LENGTH_SHORT).show()
    }

    private fun findImage(eventId: Int): String {
        for (i in 0 until events.length()){
            val event = events.getJSONObject(i)
            if (event.getInt("event_id") == eventId) {
                return event.getString("image")
            }
        }
        return ""
    }

    private fun openEventUrl() {
        if (!eventUrl.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl))
            startActivity(intent)
        } else {
            Toast.makeText(this, "URL이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}