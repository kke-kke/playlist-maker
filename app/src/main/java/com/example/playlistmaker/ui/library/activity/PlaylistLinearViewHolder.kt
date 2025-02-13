package com.example.playlistmaker.ui.library.activity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistLinearBinding
import com.example.playlistmaker.domain.library.models.Playlist

class PlaylistLinearViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding: PlaylistLinearBinding = PlaylistLinearBinding.bind(view)

    fun bind(playlist: Playlist) {
        with(binding) {
            playlistNameTextView.text = playlist.name
            songQuantity.text = outputFormat(playlist.trackCount)

            Glide.with(itemView.context)
                .load(playlist.coverUri)
                .placeholder(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(playlistCoverImage)
        }
    }

    private fun outputFormat(count: Int): String {
        val num = count % 10
        val extra = count % 100

        return if (extra in 11..19) {
            "$count треков"
        } else {
            when (num) {
                1 -> "$count трек"
                2, 3, 4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}