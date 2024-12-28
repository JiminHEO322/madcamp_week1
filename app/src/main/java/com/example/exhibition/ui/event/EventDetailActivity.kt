package com.example.exhibition.ui.event

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R


class EventDetailActivity : AppCompatActivity() {

//    private var phone: String? = null // 멤버 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "상세 정보"
        }

        val imageResId = intent.getIntExtra("EVENT_IMAGE", R.drawable.photo1)
        val title = intent.getStringExtra("EVENT_TITLE")
        val location = intent.getStringExtra("EVENT_LOCATION")
        val date = intent.getStringExtra("EVENT_DATE")

        findViewById<ImageView>(R.id.detailEventImage).setImageResource(imageResId)
        findViewById<TextView>(R.id.detailEventTitle).text = title
        findViewById<TextView>(R.id.detailEventLocation).text = location
        findViewById<TextView>(R.id.detailEventDate).text = date
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }
}