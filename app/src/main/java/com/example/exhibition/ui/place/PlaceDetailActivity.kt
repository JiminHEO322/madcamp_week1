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
    private var venue_id: Int = -1
    private var venues = JSONArray()

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


        } else {
            Toast.makeText(this, "장소 데이터를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeEventRecyclerView(exListString: String?){
        // Event RecyclerView 초기화
        eventRecyclerView = findViewById(R.id.eventRecyclerView)

        if (exListString != null) {
            val onEventsArray = JSONArray(exListString)
            val onEvents = onEventsArray.toMutableList()

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

}
