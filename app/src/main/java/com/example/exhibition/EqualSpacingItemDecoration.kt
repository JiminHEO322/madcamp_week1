package com.example.exhibition

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualSpacingItemDecoration(
    private val edgeSpacing: Int, // 화면 끝에서 간격
    private val innerSpacing: Int // 아이템 간 간격
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        // 첫 번째 아이템은 왼쪽 간격 추가
        if (position == 0) {
            outRect.left = edgeSpacing
        }

        // 마지막 아이템은 오른쪽 간격 추가
        if (position == itemCount - 1) {
            outRect.right = edgeSpacing
        }

        // 내부 간격 설정
        if (position > 0) {
            outRect.left += innerSpacing
        }
    }
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // 아이템 위치
        val column = position % spanCount // 열 번호

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount // 왼쪽 간격
            outRect.right = (column + 1) * spacing / spanCount // 오른쪽 간격

            if (position < spanCount) { // 첫 번째 행
                outRect.top = spacing
            }
            outRect.bottom = spacing // 아래쪽 간격
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing // 첫 번째 행 제외 간격 추가
            }
        }
    }
}