package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        // кнопка "назад"
        playerBinding.backButtonPlayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // получение трека из предыдущей активити
        val track = intent.getSerializableExtra("TRACK") as Track
        bindTrack(playerBinding, track)
    }

    private fun bindTrack(binding: ActivityPlayerBinding, track: Track) {
        with(binding) {
            Glide.with(binding.albumCoverImage)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(albumCoverImage)

            songNameTextView.text = track.trackName
            songArtistTextView.text = track.artistName

            songLengthValueTextView.text = SimpleDateFormat("mm:ss", java.util.Locale.getDefault()).format(track.trackTime)
            songAlbumValueTextView.text = track.collectionName.ifEmpty { "" }
            songYearValueTextView.text = track.releaseDate.take(4)
            songGenreValueTextView.text = track.primaryGenreName
            songCountryValueTextView.text = track.country

        }
    }
}