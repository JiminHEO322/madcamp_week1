package com.example.exhibition.model

data class Review(
    val imageResId: Int,    // 이미지 리소스 ID
    val title: String,      // 이벤트 제목
    val date: String,       // 날짜
    val reviewText: String  // 리뷰 텍스트
)
