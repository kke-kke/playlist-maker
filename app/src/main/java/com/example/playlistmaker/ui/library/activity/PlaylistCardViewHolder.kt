package com.example.playlistmaker.ui.library.activity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistCardBinding
import com.example.playlistmaker.domain.library.models.Playlist

class PlaylistCardViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding: PlaylistCardBinding = PlaylistCardBinding.bind(view)

    fun bind(playlist: Playlist) {
        with(binding) {
            playlistNameCard.text = playlist.name
            playlistSongCount.text = outputFormat(playlist.trackCount)

            Glide.with(itemView.context)
                .load(playlist.coverUri)
                .placeholder(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(playlistCover)
        }
    }

    private fun outputFormat(count: Int): String = when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }

}