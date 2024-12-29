package com.example.exhibition.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import com.example.exhibition.model.Place


class PlaceAdapter(private var placeList: List<Place>,
                   private val onItemClick: (Place) -> Unit,
                   private val onLikeClick: (Place) -> Unit) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var filteredList: MutableList<Place> = placeList.toMutableList()

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val exNumTextView: TextView = itemView.findViewById(R.id.exNumTextView)
        val likeImageView: ImageView = itemView.findViewById(R.id.likeImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = filteredList[position]
        holder.imageView.setImageResource(place.imageResId)
        holder.titleTextView.text = place.title
        holder.addressTextView.text = place.address
        holder.phoneTextView.text = place.phone
        holder.exNumTextView.text = place.exNum
        holder.likeImageView.setImageResource(if (place.isLike) R.drawable.icon_fulllike else R.drawable.icon_like)

        holder.itemView.setOnClickListener {
            onItemClick(place)
        }
        holder.likeImageView.setOnClickListener {
            onLikeClick(place)
        }
    }

    override fun getItemCount(): Int = filteredList.size



    fun filter(query: String) {
        val sanitizedQuery = query.replace(" ", "")
        filteredList = if (sanitizedQuery.isEmpty()) {
            placeList.toMutableList()
        } else {
            placeList.filter {
                val sanitizedTitle = it.title.replace(" ", "")
                sanitizedTitle.contains(sanitizedQuery, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateData(newPlaceList: List<Place>) {
        placeList = newPlaceList
        filteredList = placeList.toMutableList()
        notifyDataSetChanged()
    }

}
