package com.example.exhibition.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import org.json.JSONArray
import org.json.JSONObject
import android.content.Context
import android.util.Log
import com.example.exhibition.toMutableList


class PlaceAdapter(private val context: Context,
                   private var placeList: JSONArray,
                   private var events: JSONArray,
                   private val onItemClick: (JSONObject, MutableList<JSONObject>) -> Unit,
                   private val onLikeClick: (JSONObject) -> Unit) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var filteredList: MutableList<JSONObject> = placeList.toMutableList()

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
        val placeId = place.getInt("venue_id")
        val imageName = place.getString("image")
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        val exList: MutableList<JSONObject> = getEx(placeId)


        holder.imageView.setImageResource(imageResId)
        holder.titleTextView.text = place.getString("name")
        holder.addressTextView.text = place.getString("address")
        holder.phoneTextView.text = place.getString("phone")
        holder.exNumTextView.text = exList.size.toString()
        holder.likeImageView.setImageResource(if (place.getBoolean("isLike")) R.drawable.icon_fulllike else R.drawable.icon_like)

        holder.itemView.setOnClickListener {
            onItemClick(place, exList)
        }
        holder.likeImageView.setOnClickListener {
            onLikeClick(place)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        Log.d("PlaceAdapter", "filter 시작")
        val sanitizedQuery = query.replace(" ", "")
        filteredList = if (sanitizedQuery.isEmpty()) {
            placeList.toMutableList()
        } else {
            placeList.toMutableList().filter {
                val sanitizedTitle = it.getString("name").replace(" ", "")
                sanitizedTitle.contains(sanitizedQuery, ignoreCase = true) }.toMutableList()
        }

        notifyDataSetChanged()
    }

    fun updateData(newPlaceList: MutableList<JSONObject>) {
        placeList = JSONArray(newPlaceList)
        filteredList = newPlaceList
        notifyDataSetChanged()
    }


    private fun getEx(place_id: Int): MutableList<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until events.length()) {
            val event = events.getJSONObject(i)
            if (event.getInt("venue_id") ==  place_id) {
                list.add(event)
            }
        }
        return list
    }
}
