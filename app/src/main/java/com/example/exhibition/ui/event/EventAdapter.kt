package com.example.exhibition.ui.event

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
//import com.bumptech.glide.Glide


class EventAdapter(
    private val events: List<EventItem>,
    private val onItemClick: (EventItem) -> Unit
    ) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.eventImage)
        val titleView: TextView = itemView.findViewById(R.id.eventTitle)
        val locationView: TextView = itemView.findViewById(R.id.eventLocation)
        val dateView: TextView = itemView.findViewById(R.id.eventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.imageView.setImageResource(event.imageResId)
        holder.titleView.text = event.title
        holder.locationView.text = event.location
        holder.dateView.text = event.date

        holder.itemView.setOnClickListener{
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = events.size
}