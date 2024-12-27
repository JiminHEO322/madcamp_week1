package com.example.exhibition.ui.event

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R


class EventDetailActivity : AppCompatActivity() {

//    private var phone: String? = null // 멤버 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val imageResId = intent.getIntExtra("EVENT_IMAGE", R.drawable.cat1)
        val title = intent.getStringExtra("EVENT_TITLE")
        val location = intent.getStringExtra("EVENT_LOCATION")
        val date = intent.getStringExtra("EVENT_DATE")

        findViewById<ImageView>(R.id.detailEventImage).setImageResource(imageResId)
        findViewById<TextView>(R.id.detailEventTitle).text = title
        findViewById<TextView>(R.id.detailEventLocation).text = location
        findViewById<TextView>(R.id.detailEventDate).text = date

//        val title = intent.getStringExtra("PLACE_TITLE")
//        val addressAll = intent.getStringExtra("PLACE_ADDRESS")
//        phone = intent.getStringExtra("PLACE_PHONE")
//        val exNum = intent.getStringExtra("PLACE_EXNUM")
//        val imageResId = intent.getIntExtra("PLACE_IMAGE", R.drawable.sample_image)
//
//
//        findViewById<TextView>(R.id.detailTitleTextView).text = title
//        findViewById<TextView>(R.id.detailAddressTextView).text = addressAll
//        //findViewById<TextView>(R.id.detailPhoneTextView).text = phone
//        //findViewById<TextView>(R.id.detailExNumTextView).text = exNum
//        findViewById<ImageView>(R.id.detailImageView).setImageResource(imageResId)
    }
}