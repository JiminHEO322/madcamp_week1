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


class PlaceDetailActivity : AppCompatActivity() {

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
