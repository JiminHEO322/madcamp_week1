package com.example.exhibition.ui.mypage

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import com.example.exhibition.model.Review


class ReviewDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val imageResId = intent.getIntExtra("REVIEW_IMAGE", R.drawable.photo1)
        val title = intent.getStringExtra("REVIEW_TITLE")

        findViewById<ImageView>(R.id.detailReviewImage).setImageResource(imageResId)
        findViewById<TextView>(R.id.detailReviewTitle).text = title
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }
}