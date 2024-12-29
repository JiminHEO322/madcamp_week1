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


class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "상세 정보"
        }

        val jsonString = intent.getStringExtra("event_data")
        val location = intent.getStringExtra("event_location")

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val title = jsonObject.getString("title")
            val date = jsonObject.getString("date")
            val imageName = jsonObject.getString("image")
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

            findViewById<ImageView>(R.id.detailEventImage).setImageResource(imageResId)
            findViewById<TextView>(R.id.detailEventTitle).text = title
            findViewById<TextView>(R.id.detailEventLocation).text = location
            findViewById<TextView>(R.id.detailEventDate).text = date
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }
}