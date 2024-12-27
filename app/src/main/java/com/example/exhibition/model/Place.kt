package com.example.exhibition.model

data class Place (
    val imageResId: Int,  // 이미지 리소스 ID
    val title: String,    // 장소 이름
    val address: String,  // 주소
    val phone: String,    // 전화번호
    val exNum: String,   // 진행 중인 공연 개수
    val addressAll: String, // 전체 주소
)