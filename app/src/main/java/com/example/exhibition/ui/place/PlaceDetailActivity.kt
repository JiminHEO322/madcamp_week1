package com.example.exhibition.ui.place

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import android.widget.TextView
import android.widget.ImageView
import android.view.View
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.ui.event.EventAdapter
import com.example.exhibition.ui.event.EventItem
import com.example.exhibition.ui.event.EventDetailActivity
import org.json.JSONObject
import org.json.JSONArray
import java.io.InputStream


class PlaceDetailActivity : AppCompatActivity() {

    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter

    private var phone: String? = null
    private var url: String? = null
    private var instagram: String? = null
    private var youtube: String? = null
    private var isLike: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        val title = intent.getStringExtra("PLACE_TITLE")
        val addressAll = intent.getStringExtra("PLACE_ADDRESS")
        val exNum = intent.getStringExtra("PLACE_EXNUM")
        val imageResId = intent.getIntExtra("PLACE_IMAGE", R.drawable.yesul)
        val venue_id = 1

        phone = intent.getStringExtra("PLACE_PHONE")
        url = intent.getStringExtra("PLACE_URL")
        instagram = intent.getStringExtra("PLACE_INSTAGRAM")
        youtube = intent.getStringExtra("PLACE_YOUTUBE")
        isLike = intent.getBooleanExtra("PLACE_ISLIKE", false)


        findViewById<TextView>(R.id.detailTitleTextView).text = title
        findViewById<TextView>(R.id.detailAddressTextView).text = addressAll
        //findViewById<TextView>(R.id.detailPhoneTextView).text = phone
        //findViewById<TextView>(R.id.detailExNumTextView).text = exNum
        findViewById<ImageView>(R.id.detailImageView).setImageResource(imageResId)
        findViewById<ImageView>(R.id.detailLikeImageView).setImageResource(if (isLike) R.drawable.icon_fulllike else R.drawable.icon_like)



        // Event RecyclerView 초기화
        eventRecyclerView = findViewById(R.id.eventRecyclerView)

        // 샘플 데이터 생성
        val jsonString = loadJSONFromAsset(this, "exhibition_data.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val venues = jsonObject.getJSONArray("venues")
            val events = jsonObject.getJSONArray("events")

            val onEvents = mutableListOf<JSONObject>()
            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                if (event.getInt("venue_id") == venue_id) onEvents.add(event)
            }

            // RecyclerView와 Adapter 설정
            eventAdapter = EventAdapter(this, venues, onEvents) { selectedEvent ->
                // 클릭 이벤트 처리
                val selectedVenue = getVenueLocation(venues, selectedEvent.getInt("venue_id"))
                val intent = Intent(this, EventDetailActivity::class.java).apply {
                    putExtra("event_data", selectedEvent.toString())
                    putExtra("event_location", selectedVenue)
                }
                startActivity(intent)
            }

            eventRecyclerView.layoutManager = GridLayoutManager(this, 2)
            eventRecyclerView.adapter = eventAdapter
        } else {
            Toast.makeText(this, "JSON 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
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

    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getVenueLocation(venues: JSONArray, venueID: Int): String? {
        for (i in 0 until venues.length()){
            val venue = venues.getJSONObject(i)
            if (venue.getInt("venue_id") == venueID) {
                return venue.getString("name")
            }
        }
        return "-"
    }
}
