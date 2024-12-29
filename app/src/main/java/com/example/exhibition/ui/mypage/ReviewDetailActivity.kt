package com.example.exhibition.ui.mypage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exhibition.R
import com.example.exhibition.model.Review
import android.content.Context
import org.json.JSONObject


class ReviewDetailActivity : AppCompatActivity() {

    private lateinit var date_textView: TextView
    private lateinit var date_editText: EditText
    private lateinit var content_textView: TextView
    private lateinit var content_editText: EditText
    private var isEditing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val reviewJsonString = intent.getStringExtra("review_data")
        
        // 수정 필요
        val review = JSONObject(reviewJsonString)
        val imageName = review.getString("image")
        val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

        findViewById<ImageView>(R.id.detailReviewImage).setImageResource(imageResId)
        findViewById<TextView>(R.id.detailReviewTitle).text = title

        // 뷰 초기화
        date_textView = findViewById(R.id.detailReviewDate)
        date_editText = findViewById(R.id.editDate)
        content_textView = findViewById(R.id.detailReviewContent)
        content_editText = findViewById(R.id.editContent)

        date_editText.visibility = android.view.View.GONE
        content_editText.visibility = android.view.View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 Activity 종료
        return true
    }

    // 메뉴를 생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_review, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_edit)?.isVisible = !isEditing
        menu?.findItem(R.id.action_save)?.isVisible = isEditing
        return super.onPrepareOptionsMenu(menu)
    }

    // 메뉴 클릭 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                // 수정 모드
                isEditing = true
                toggleEditMode(isEditing)
                invalidateOptionsMenu()
                return true
            }

            R.id.action_save -> {
                // 저장 모드
                isEditing = false
                saveText()
                toggleEditMode(isEditing)
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun toggleEditMode(enable: Boolean) {
        if (enable) {
            date_editText.visibility = android.view.View.VISIBLE
            date_textView.visibility = android.view.View.GONE
            date_editText.setText(date_textView.text) // 기존 텍스트를 EditText로 설정

            content_editText.visibility = android.view.View.VISIBLE
            content_textView.visibility = android.view.View.GONE
            content_editText.setText(content_textView.text) // 기존 텍스트를 EditText로 설정
        } else {
            date_editText.visibility = android.view.View.GONE
            date_textView.visibility = android.view.View.VISIBLE

            content_editText.visibility = android.view.View.GONE
            content_textView.visibility = android.view.View.VISIBLE
        }
    }

    private fun saveText() {
        // 수정된 텍스트를 TextView에 반영
        date_textView.text = date_editText.text.toString()
        content_textView.text = content_editText.text.toString()
    }
}