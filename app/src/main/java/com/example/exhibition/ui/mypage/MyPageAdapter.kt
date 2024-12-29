package com.example.exhibition.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import com.example.exhibition.model.Review
import org.json.JSONArray
import org.json.JSONObject

class MyPageAdapter(
    private val context: Context,
    private val reviews: MutableList<JSONObject>,
    private val onItemClick: (JSONObject) -> Unit
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
        val event: JSONObject = reviews[position]
        val imageName = event.getString("image")
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        holder.imageView.setImageResource(imageResId)

        holder.itemView.setOnClickListener{
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = reviews.size
}