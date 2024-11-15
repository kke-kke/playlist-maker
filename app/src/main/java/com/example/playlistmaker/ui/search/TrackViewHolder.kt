package com.example.playlistmaker.ui.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchResultBinding
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding: SearchResultBinding = SearchResultBinding.bind(itemView)

    fun bind(track: Track) {
        with(binding) {
            songNameTextView.text = track.trackName
            songArtistTextView.text = track.artistName
            songLengthTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)

            Glide.with(itemView.context)
                .load(track.artworkUrl)
                .placeholder(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(albumCoverImage)

            songArtistTextView.invalidate()
            songArtistTextView.requestLayout()
        }

    }
}