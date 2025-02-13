package com.example.playlistmaker.ui.library.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.library.models.Playlist

class PlaylistLinearAdapter() : RecyclerView.Adapter<PlaylistLinearViewHolder>() {
    var playlists = ArrayList<Playlist>()
    var onPlaylistClick : ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistLinearViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_linear, parent, false)
        return PlaylistLinearViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistLinearViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

}