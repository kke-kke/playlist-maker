package com.example.playlistmaker.ui.library.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.library.models.Playlist

class PlaylistCardAdapter(): RecyclerView.Adapter<PlaylistCardViewHolder>() {
    var playlists = ArrayList<Playlist>()
    var onItemClick : ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_card, parent, false)
        return PlaylistCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistCardViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlists[position])
        }
    }
}