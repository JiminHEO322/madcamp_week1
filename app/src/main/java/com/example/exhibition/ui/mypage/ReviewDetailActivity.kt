package com.example.exhibition.ui.mypage

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
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
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.util.Calendar
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class ReviewDetailActivity : AppCompatActivity() {

    private lateinit var reviews: JSONArray
    private lateinit var events: JSONArray

    private lateinit var imageView: ImageView
    private lateinit var date_textView: TextView
    private lateinit var date_editText: TextView
    private lateinit var summary_textView: TextView
    private lateinit var summary_editText: EditText
    private lateinit var content_textView: TextView
    private lateinit var content_editText: EditText

    private var reviewId : Int = 0
    private var isEditing: Boolean = false

    private val fileName: String = "exhibition_data.json"

    private lateinit var changeImageButton: ImageView

    // 사용자 권한 처리
    private val REQUEST_PERMISSION = 10

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        // UI 초기화
        imageView = findViewById(R.id.detailReviewImage)
        changeImageButton = findViewById(R.id.changeImageButton)
        Log.d("ReviewDetailActivity", "ImageView 및 ChangeImageButton 초기화 완료")

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let { uri ->
                    // 선택된 사진 표시
                    val bitmap = getBitmapFromUri(uri)
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        val byteArray = bitmapToByteArray(bitmap)
                        saveImageToJSON(byteArray) // 이미지 데이터를 JSON에 저장
                    }
                }
            }
        }

        val jsonString = initializeDefaultJSON(this)

        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            events = jsonObject.getJSONArray("events")
            reviews = jsonObject.getJSONArray("reviews")

            reviewId = intent.getIntExtra("review_id", 0)
            Log.d("ReviewDetailActivity", "받은 reviewIdString: $reviewId")

            val review = findReview(reviewId)
            val event = findEvent(reviewId)

            val imageName = review.optString("image", "photo1")
            val imageResId = resources.getIdentifier(imageName, "drawable", packageName)

            findViewById<ImageView>(R.id.detailReviewImage).setImageResource(imageResId)
            findViewById<TextView>(R.id.detailReviewTitle).text = event.getString("title")

            loadImageFromJSON()

            // 뷰 초기화
            date_textView = findViewById(R.id.detailReviewDate)
            date_editText = findViewById(R.id.editDate)
            summary_textView = findViewById(R.id.detailReviewSummary)
            summary_editText = findViewById(R.id.editSummary)
            content_textView = findViewById(R.id.detailReviewContent)
            content_editText = findViewById(R.id.editContent)

            date_editText.visibility = android.view.View.GONE
            summary_editText.visibility = android.view.View.GONE
            content_editText.visibility = android.view.View.GONE
            changeImageButton.visibility = android.view.View.GONE

            date_textView.text = review.optString("date", "")
            summary_textView.text = review.optString("summary", "")
            content_textView.text = review.optString("content", "")

            // 이미지 수정
            changeImageButton.setOnClickListener{
                checkPermission()
                changeImage()
                Log.d("ReviewDetailActivity", "이미지 변경완료 $reviews")
//
//                loadImageFromJSON()
            }

            date_editText.setOnClickListener{
                showDatePickerDialog()
            }
        }
    }

    private fun changeImage() {
//        val dialog = AlertDialog.Builder(this)
//        val view = changeImageButton
//        dialog.setContentView(view)
//
//        // 팝업 내 버튼 이벤트
//        val posterButton = view.findViewById<TextView>(R.id.btn_poster)
//        val addPhotoButton = view.findViewById<TextView>(R.id.btn_add_photo)
//
//        posterButton.setOnClickListener {
//            // 포스터 클릭 로직
//            for (i in 0 until events.length()){
//                    val event = events.getJSONObject(i)
//                    if (event.getInt("event_id") == reviewId){
//                        for (j in 0 until reviews.length()){
//                            val review = reviews.getJSONObject(j)
//                            if (review.getInt("review_id") == reviewId){
//                                review.put("image", event.getString("image"))
//
//                                saveUpdatedJSON()
//                                break
//                            }
//                        }
//                    }
//            }
//            loadImageFromJSON()
//        }
//
//        addPhotoButton.setOnClickListener {
//            // 사진 추가 클릭 로직
//            try{
//                openGallery()
//            } catch(e: Exception){
//                Log.e("ReviewDetailActivity", "이미지 수정 중 오류 발생: ${e.message}")
//                Toast.makeText(this, "수정 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
        AlertDialog.Builder(this)
            .setTitle("이미지 변경")
            .setMessage("변경할 이미지")
            .setPositiveButton("갤러리") { _, _ ->
                try {
                    openGallery()
                } catch (e: Exception) {
                    Log.e("ReviewDetailActivity", "이미지 수정 중 오류 발생: ${e.message}")
                    Toast.makeText(this, "수정 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("포스터") { _, _ ->
                for (i in 0 until events.length()){
                    val event = events.getJSONObject(i)
                    if (event.getInt("event_id") == reviewId){
                        for (j in 0 until reviews.length()){
                            val review = reviews.getJSONObject(j)
                            if (review.getInt("review_id") == reviewId){
                                review.put("image", event.getString("image"))

                                saveUpdatedJSON()
                                break
                            }
                        }
                    }
                }
                loadImageFromJSON()
            }
            .show()
    }

    private fun showDatePickerDialog() {
        // 현재 날짜로 Calendar 객체 생성
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 생성 및 리스너 등록
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // 선택된 날짜 정보를 TextView에 반영
                // (month는 0부터 시작하므로 +1 해주어야 실제 월과 일치)
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                date_editText.text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()
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

            R.id.action_delete -> {
                deleteReview()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReview() {
        Log.d("ReviewDetailActivity", "delete")
        AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("이 리뷰를 정말로 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                try {
                    // 리뷰 삭제
                    for (i in 0 until reviews.length()) {
                        val review = reviews.getJSONObject(i)
                        if (review.getInt("review_id") == reviewId) {
                            reviews.remove(i) // 리뷰 삭제

                            for (j in 0 until events.length()){
                                val event = events.getJSONObject(i)
                                if (event.getInt("event_id") == reviewId) {
                                    event.put("viewed", false)
                                    break
                                }
                            }
                            Log.d("ReviewDetailAcitivty", "리뷰 삭제 완료")
                            saveUpdatedJSON() // JSON 파일 업데이트

                            Toast.makeText(this, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            finish() // 액티비티 종료
                            return@setPositiveButton
                        }
                    }
                    Toast.makeText(this, "리뷰를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("ReviewDetailActivity", "리뷰 삭제 중 오류 발생: ${e.message}")
                    Toast.makeText(this, "삭제 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss() // 팝업 닫기
            }
            .show()
    }


    private fun toggleEditMode(enable: Boolean) {
        if (enable) {
            date_editText.visibility = android.view.View.VISIBLE
            date_textView.visibility = android.view.View.GONE
            date_editText.setText(date_textView.text) // 기존 텍스트를 EditText로 설정

            summary_editText.visibility = android.view.View.VISIBLE
            summary_textView.visibility = android.view.View.GONE
            summary_editText.setText(summary_textView.text) // 기존 텍스트를 EditText로 설정

            content_editText.visibility = android.view.View.VISIBLE
            content_textView.visibility = android.view.View.GONE
            content_editText.setText(content_textView.text) // 기존 텍스트를 EditText로 설정

            changeImageButton.visibility = android.view.View.VISIBLE
        } else {
            date_editText.visibility = android.view.View.GONE
            date_textView.visibility = android.view.View.VISIBLE

            summary_editText.visibility = android.view.View.GONE
            summary_textView.visibility = android.view.View.VISIBLE

            content_editText.visibility = android.view.View.GONE
            content_textView.visibility = android.view.View.VISIBLE

            changeImageButton.visibility = android.view.View.GONE
            Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveText() {
        // 수정된 텍스트를 TextView에 반영
        date_textView.text = date_editText.text.toString()
        summary_textView.text = summary_editText.text.toString()
        content_textView.text = content_editText.text.toString()

        // 수정된 텍스트를 JSON 파일에 반영
        for (i in 0 until reviews.length()) {
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId) {
                val date = date_editText.text.toString()
                val summary = summary_editText.text.toString()
                val content = content_editText.text.toString()

                review.put("date", date)
                review.put("summary", summary)
                review.put("content", content)

                saveUpdatedJSON()
                Log.d("EventFragment", "이벤트 상태 변경 및 저장 완료: ${review.toString(2)}")

                break
            }
        }
    }

    private fun findReview(reviewId: Int): JSONObject {
        for (i in 0 until reviews.length()){
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId){
                return review
            }
        }
        return JSONObject()
    }

    private fun findEvent(eventId: Int): JSONObject {
        for (i in 0 until events.length()){
            val event = events.getJSONObject(i)
            if (event.getInt("event_id") == eventId){
                return event
            }
        }
        return JSONObject()
    }

    private fun initializeDefaultJSON(context: Context): String? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            try {
                val inputStream: InputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                val defaultJson = String(buffer, Charsets.UTF_8)
                saveJSONToFile(context, defaultJson) // 파일 저장
                Log.d("PlaceFragment", "기본 JSON 파일 생성 완료")
                return defaultJson
            } catch (e: Exception) {
                Log.e("PlaceFragment", "기본 JSON 파일 초기화 중 오류 발생: ${e.message}")
            }
        }
        return loadJSON(context) // 파일이 있으면 로드
    }

    private fun loadJSON(context: Context): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonData = file.readText()
                Log.d("ReviewDetailActivity", "로드된 JSON 데이터: $jsonData") // 확인용 로그
                jsonData
            } else {
                Log.w("ReviewDetailActivity", "JSON 파일이 존재하지 않습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 로드 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun saveUpdatedJSON() {
        try {
            // 기존 JSON 데이터 읽기
            val jsonString = loadJSON(this)
            val jsonObject = if (jsonString != null) {
                JSONObject(jsonString)
            } else {
                JSONObject().apply {
                    put("venues", JSONArray())
                    put("events", JSONArray())
                    put("reviews", JSONArray())
                }
            }

            jsonObject.put("events", events)
            jsonObject.put("reviews", reviews)

            // 파일에 저장
            saveJSONToFile(this, jsonObject.toString())
            Log.d("ReviewDetailActivity", "JSON 업데이트 완료: ${jsonObject.toString(2)}") // 확인용 로그
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 업데이트 중 오류 발생: ${e.message}")
            Toast.makeText(this, "데이터 저장 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveJSONToFile(context: Context, jsonString: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d("ReviewDetailActivity", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "JSON 파일 저장 중 오류 발생: ${e.message}")
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
        } else {
            // 이미 권한이 허용되어 있음
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openGallery()
            } else {
                // 권한 거부됨
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return@use null
            }
            // !! BUT !! 여기서 exifInterface를 재사용하려면, inputStream을 다시 열거나
            // rotateBitmapIfRequired() 함수 내부에서 열어야 합니다.

            // 1) decodeStream으로 Bitmap 생성
            val originalBitmap = contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return null

            // 2) EXIF 정보 읽어 회전 보정
            val correctedBitmap = rotateBitmapIfRequired(originalBitmap, uri, contentResolver)

            correctedBitmap
        } catch (e: Exception) {
            Log.e("ReviewDetailActivity", "Bitmap 변환 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun rotateBitmapIfRequired(originalBitmap: Bitmap, uri: Uri, contentResolver: ContentResolver?): Bitmap? {
        // 1. EXIF 정보 가져오기
        val inputStream = contentResolver?.openInputStream(uri) ?: return originalBitmap
        val exif = ExifInterface(inputStream)
        inputStream.close()

        // 2. orientation 값 확인
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        // 3. 회전 각도 결정
        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        // 4. 회전할 필요가 없으면 원본 그대로 반환
        if (rotationDegrees == 0) {
            return originalBitmap
        }

        // 5. 회전 매트릭스 적용
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())

        // 6. 회전된 비트맵 생성
        return Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )
    }

    private fun loadImageFromJSON() {
        for (i in 0 until reviews.length()) {
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId) {
                val imageValue = review.optString("image", null)
                val imageResId = resources.getIdentifier(imageValue, "drawable", packageName)
                if (imageResId != 0){
                    // drawable 리소스 이름인 경우
                    imageView.setImageResource(imageResId)
                    Log.d("ReviewDetailActivity", "이미지(Drawable) 로드 완료")
                } else{
                    val byteArray = Base64.decode(imageValue, Base64.DEFAULT)
                    val bitmap = byteArrayToBitmap(byteArray)
                    imageView.setImageBitmap(bitmap)
                    Log.d("ReviewDetailActivity", "이미지(Base64) 로드 완료")
                }

                break
            }
        }
    }

    private fun saveImageToJSON(byteArray: ByteArray) {
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        for (i in 0 until reviews.length()) {
            val review = reviews.getJSONObject(i)
            if (review.getInt("review_id") == reviewId) {
                review.put("image", base64String)
                saveUpdatedJSON()
                Log.d("ReviewDetailActivity", "이미지 저장 완료")
                break
            }
        }
    }
}