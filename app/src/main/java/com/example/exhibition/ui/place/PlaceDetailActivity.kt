package com.example.exhibition.ui.place

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import com.example.exhibition.getVenueLocation
import com.example.exhibition.toMutableList
import android.widget.TextView
import android.widget.ImageView
import android.view.View
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.ui.event.EventAdapter
import com.example.exhibition.ui.event.EventItem
import com.example.exhibition.ui.event.EventDetailActivity
import org.json.JSONObject
import org.json.JSONArray
import java.io.File
import java.io.InputStream


class PlaceDetailActivity : AppCompatActivity() {

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    private lateinit var likeImageView: ImageView

    private var phone: String? = null
    private var url: String? = null
    private var instagram: String? = null
    private var youtube: String? = null
    private var isLike: Boolean = false
    private var venue_id: Int = -1
    private var venues = JSONArray()

    private val fileName: String = "exhibition_data.json"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "상세 정보"
        }

        val placeJsonString = intent.getStringExtra("place_data")
        val exListString = intent.getStringExtra("ex_list")
        val venuesString = intent.getStringExtra("venues")
        venues = JSONArray(venuesString)

        if (placeJsonString != null) {
            val placeJsonObject = JSONObject(placeJsonString)

            val title = placeJsonObject.getString("name")
            val addressAll = placeJsonObject.getString("addressAll")
            val imageName = placeJsonObject.getString("image")
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
            venue_id = placeJsonObject.getInt("venue_id")
            phone = placeJsonObject.getString("phone")
            url = placeJsonObject.getString("url")
            instagram = placeJsonObject.getString("instagram")
            youtube = placeJsonObject.getString("youtube")
            isLike = placeJsonObject.getBoolean("isLike")

            findViewById<TextView>(R.id.detailTitleTextView).text = title
            findViewById<TextView>(R.id.detailAddressTextView).text = addressAll
            //findViewById<TextView>(R.id.detailPhoneTextView).text = phone
            //findViewById<TextView>(R.id.detailExNumTextView).text = exNum
            findViewById<ImageView>(R.id.detailImageView).setImageResource(imageResId)
            findViewById<ImageView>(R.id.detailLikeImageView).setImageResource(if (isLike) R.drawable.icon_fulllike else R.drawable.icon_like)


            initializeEventRecyclerView(exListString)

            likeImageView = findViewById<ImageView>(R.id.detailLikeImageView)
            likeImageView.setOnClickListener{
                toggleState(venue_id)
            }


        } else {
            Toast.makeText(this, "장소 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleState(venueId: Int){
        for (i in 0 until venues.length()){
            val venue = venues.getJSONObject(i)
            if (venue.getInt("venue_id") == venueId) {
                val currentLikeState = venue.getBoolean("isLike")
                val newLikeState = !currentLikeState
                venue.put("isLike", newLikeState)

                isLike = newLikeState
                likeImageView.setImageResource(if (isLike) R.drawable.icon_fulllike else R.drawable.icon_like)

                saveUpdatedJSON()
                Log.d("PlaceDetailActivity", "좋아요 상태 변경 및 저장 완료: ${venue.toString(2)}")

                break
            }
        }
    }
    private fun initializeEventRecyclerView(exListString: String?){
        // Event RecyclerView 초기화
        eventRecyclerView = findViewById(R.id.eventRecyclerView)

        if (exListString != null) {
            val onEventsArray = JSONArray(exListString)
            val onEvents = onEventsArray.toMutableList()

            // RecyclerView와 Adapter 설정
            eventAdapter = EventAdapter(this, venues, onEvents, { selectedEvent ->
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(this, EventDetailActivity::class.java).apply {
                    putExtra("event_id", selectedEvent.getInt("event_id"))
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }, isFullWidth = true) // match_parent로 설정

            eventRecyclerView.layoutManager = GridLayoutManager(this, 2)
            eventRecyclerView.adapter = eventAdapter
        } else {
            Toast.makeText(this, "진행 중인 전시/공연이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    fun onPhoneClick(view: View) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }

    fun onHomeClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    fun onInstgramClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            if (instagram == "null") {
                Toast.makeText(this@PlaceDetailActivity, "인스타 계정이 없습니다", Toast.LENGTH_SHORT).show()
                return
            }
            data = Uri.parse(instagram)
        }
        startActivity(intent)
    }

    fun onYoutubeClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            if (youtube == "null") {
                Toast.makeText(this@PlaceDetailActivity, "유튜브 계정이 없습니다", Toast.LENGTH_SHORT).show()
                return
            }
            data = Uri.parse(youtube)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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

            jsonObject.put("venues", venues)

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

}
