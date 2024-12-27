package com.example.exhibition.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import com.example.exhibition.model.Place


class PlaceAdapter(private val placeList: List<Place>, private val onItemClick: (Place) -> Unit) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val exNumTextView: TextView = itemView.findViewById(R.id.exNumTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placeList[position]
        holder.imageView.setImageResource(place.imageResId)
        holder.titleTextView.text = place.title
        holder.addressTextView.text = place.address
        holder.phoneTextView.text = place.phone
        holder.exNumTextView.text = place.exNum

        holder.itemView.setOnClickListener {
            onItemClick(place)
        }
    }

    override fun getItemCount(): Int = placeList.size
}
