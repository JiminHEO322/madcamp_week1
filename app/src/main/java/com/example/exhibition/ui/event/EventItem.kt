package com.example.exhibition.ui.event

data class EventItem(
    val imageResId: Int,  // 이미지 리소스 ID
    val title: String,    // 이벤트 제목
    val location: String, // 장소
    val date: String      // 날짜
)
