package com.example.exhibition.ui.place

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exhibition.R
import android.widget.TextView
import android.widget.ImageView
import android.view.View
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.ui.event.EventAdapter
import com.example.exhibition.ui.event.EventItem
import com.example.exhibition.ui.event.EventDetailActivity


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
        val eventList = listOf(
            EventItem(R.drawable.photo1, "우연히 웨스 앤더슨 2", "그라운드시소 센트럴", "2024.10.18 - 2025.04.13"),
            EventItem(R.drawable.photo2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.photo3, "피아노 파 드 되", "유니버설아트센터", "2025.02.09"),
            EventItem(R.drawable.photo1, "우연히 웨스 앤더슨 2", "그라운드시소 센트럴", "2024.10.18 - 2025.04.13"),
            EventItem(R.drawable.photo2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.photo3, "피아노 파 드 되", "유니버설아트센터", "2025.02.09")
        )

        // RecyclerView와 Adapter 설정
        eventAdapter = EventAdapter(eventList) { selectedEvent ->
            // 클릭 이벤트 처리
            val intent = Intent(this, EventDetailActivity::class.java).apply {
                putExtra("EVENT_IMAGE", selectedEvent.imageResId)
                putExtra("EVENT_TITLE", selectedEvent.title)
                putExtra("EVENT_LOCATION", selectedEvent.location)
                putExtra("EVENT_DATE", selectedEvent.date)
            }
            startActivity(intent)
        }

        eventRecyclerView.layoutManager = GridLayoutManager(this,2)
        eventRecyclerView.adapter = eventAdapter
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
}
