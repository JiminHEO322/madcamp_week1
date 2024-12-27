package com.example.exhibition.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {

    private val _images = MutableLiveData<List<String>>() // 예제: 문자열 리스트
    val images: LiveData<List<String>> = _images

    init {
        _images.value = listOf(
            "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/05/15231647/20240515ceva_experts4.jpg",
            "https://i.namu.wiki/i/IhFrc6uiSNlonNFRXzSNrKrhPKrjpmlmsB_SDg3x0PeW_L06BFuF7mOq8AcPDYjonfNpG64cQYsINU8sICeDpg.webp",
            "https://www.fitpetmall.com/wp-content/uploads/2023/09/shutterstock_2205178589-1-1.png",
            "https://cdn.dailyvet.co.kr/wp-content/uploads/2024/08/30205213/20240830ceva_experts3.jpg")
    }
}