package com.example.exhibition

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import com.example.exhibition.MainActivity
import android.view.View


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val pictureImage = findViewById<ImageView>(R.id.pictureImage)
        val pictureCutImage = findViewById<ImageView>(R.id.pictureCutImage)

        // 그림 이미지가 아래로 내려가는 애니메이션
        val animator = ObjectAnimator.ofFloat(pictureImage, "translationY", 380f)
        animator.duration = 1000 // 애니메이션 지속 시간 (2초)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

        val animatorCut = ObjectAnimator.ofFloat(pictureCutImage, "translationY", 380f)
        animatorCut.duration = 1000 // 애니메이션 지속 시간 (2초)
        animatorCut.interpolator = AccelerateDecelerateInterpolator()
        animatorCut.start()

        // 일정 시간 후 메인 화면으로 이동
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500) // 2.5초 후 실행
    }
}
