package com.example.exhibition.ui.event

import android.content.Context
import android.media.metrics.Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import org.json.JSONArray
import org.json.JSONObject


class EventAdapter(
    private val context: Context,
    private val venues: JSONArray,
    private val events: JSONArray,
    private val onItemClick: (JSONObject) -> Unit
    ) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.eventImage)
        val titleView: TextView = itemView.findViewById(R.id.eventTitle)
        val locationView: TextView = itemView.findViewById(R.id.eventLocation)
        val dateView: TextView = itemView.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event: JSONObject = events.getJSONObject(position)
        val imageName = event.getString("image")
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        holder.imageView.setImageResource(imageResId)
        holder.titleView.text = event.getString("title")
        holder.locationView.text = getVenueLocation(venues, event.getInt("venue_id"))
        holder.dateView.text = event.getString("date")

        holder.itemView.setOnClickListener{
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = events.length()

    private fun getVenueLocation(venues: JSONArray, venueID: Int): String? {
        for (i in 0 until venues.length()){
            val venue = venues.getJSONObject(i)
            if (venue.getInt("venue_id") == venueID) {
                return venue.getString("name")
            }
        }
        return "-"
    }
}