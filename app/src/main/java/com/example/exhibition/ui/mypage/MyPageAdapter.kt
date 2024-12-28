package com.example.exhibition.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import com.example.exhibition.model.Review
import com.example.exhibition.ui.event.EventAdapter.EventViewHolder

class MyPageAdapter (
    private val reviews: List<Review>,
    private val onItemClick: (Review) -> Unit
    ) : RecyclerView.Adapter<MyPageAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.reviewImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val event = reviews[position]
        holder.imageView.setImageResource(event.imageResId)

        holder.itemView.setOnClickListener{
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = reviews.size
}