package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackSearchHistoryAdapter : RecyclerView.Adapter<TrackViewHolder> () {
    var tracks = ArrayList<Track>()
    var onItemClick : ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val reverseIndex = tracks.size - 1 - position
        holder.bind(tracks[reverseIndex])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tracks[reverseIndex])
        }
    }
}