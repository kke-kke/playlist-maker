package com.example.playlistmaker.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var playerBinding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        val track = intent.getSerializableExtra("TRACK") as Track
        viewModel = ViewModelProvider(this, PlayerViewModelFactory())[PlayerViewModel::class.java]

        viewModel.loadTrack(track)
        bindTrack(playerBinding, track)

        viewModel.currentPosition.observe(this) { position ->
            playerBinding.currentLengthTextView.text = position
        }

        viewModel.playerState.observe(this) { state ->
            changePlayButton(state)
        }

        initClickListeners()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
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

            songLengthValueTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
            songAlbumValueTextView.text = track.collectionName.ifEmpty { "" }
            songYearValueTextView.text = track.releaseDate.take(4)
            songGenreValueTextView.text = track.primaryGenreName
            songCountryValueTextView.text = track.country
        }
    }

    private fun initClickListeners() {
        playerBinding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        playerBinding.playerToolbar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun changePlayButton(state: PlayerState) {
        when (state) {
            PlayerState.Playing -> {
                playerBinding.playButton.setImageResource(R.drawable.pause)
                playerBinding.playButton.isEnabled = true
            }
            PlayerState.Paused, PlayerState.Prepared -> {
                playerBinding.playButton.setImageResource(R.drawable.play)
                playerBinding.playButton.isEnabled = true
            }
            PlayerState.Default -> {
                playerBinding.playButton.setImageResource(R.drawable.play)
                playerBinding.playButton.isEnabled = false
            }
        }
    }
}
