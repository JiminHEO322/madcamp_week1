package com.example.exhibition.ui.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.R
import org.json.JSONArray
import org.json.JSONObject
import com.example.exhibition.getVenueLocation


class EventAdapter(
    private val context: Context,
    private val venues: JSONArray,
    private val events: MutableList<JSONObject>,
    private val onItemClick: (JSONObject) -> Unit,
    private val isFullWidth: Boolean
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

        if (isFullWidth) {
            val layoutParams = view.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT // match_parent 설정
            view.layoutParams = layoutParams
        }

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event: JSONObject = events[position]
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

    override fun getItemCount(): Int = events.size
}