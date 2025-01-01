package com.example.exhibition.ui.mypage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
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
        val review: JSONObject = reviews[position]
        val imageName = if (review.has("image")) review.getString("image") else "default_image"

        setImage(holder, imageName)
        holder.itemView.setOnClickListener{
            onItemClick(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    private fun setImage(holder: ReviewViewHolder, imageName: String) {
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (imageResId != 0){
            // drawable 리소스 이름인 경우
            holder.imageView.setImageResource(imageResId)
            Log.d("ReviewDetailActivity", "이미지(Drawable) 로드 완료")
        } else{
            val byteArray = Base64.decode(imageName, Base64.DEFAULT)
            val bitmap = byteArrayToBitmap(byteArray)
            holder.imageView.setImageBitmap(bitmap)
            Log.d("ReviewDetailActivity", "이미지(Base64) 로드 완료")
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}