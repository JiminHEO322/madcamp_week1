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


class PlaceDetailActivity : AppCompatActivity() {

    private var phone: String? = null // 멤버 변수로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        val title = intent.getStringExtra("PLACE_TITLE")
        val addressAll = intent.getStringExtra("PLACE_ADDRESS")
        phone = intent.getStringExtra("PLACE_PHONE")
        val exNum = intent.getStringExtra("PLACE_EXNUM")
        val imageResId = intent.getIntExtra("PLACE_IMAGE", R.drawable.sample_image)


        findViewById<TextView>(R.id.detailTitleTextView).text = title
        findViewById<TextView>(R.id.detailAddressTextView).text = addressAll
        //findViewById<TextView>(R.id.detailPhoneTextView).text = phone
        //findViewById<TextView>(R.id.detailExNumTextView).text = exNum
        findViewById<ImageView>(R.id.detailImageView).setImageResource(imageResId)
    }

    fun onPhoneClick(view: View) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse(phone)
        }
        startActivity(intent)
    }

    fun onHomeClick(view: View) {
        val url = "https://www.sac.or.kr/"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}
