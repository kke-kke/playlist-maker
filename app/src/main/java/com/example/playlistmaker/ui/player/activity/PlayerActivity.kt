package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.player.viewModel.PlayerViewModel
import com.example.playlistmaker.ui.player.viewModel.PlayerViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerBinding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        val track = intent.getSerializableExtra("TRACK") as Track
        playerViewModel = ViewModelProvider(this, PlayerViewModelFactory())[PlayerViewModel::class.java]

        playerViewModel.loadTrack(track)
        bindTrack(playerBinding, track)

        playerViewModel.playerState.observe(this) { state ->
            with(playerBinding) {
                playButton.setImageResource(if (state.isPlaying) R.drawable.pause else R.drawable.play)
                playButton.isEnabled = state.isPrepared
                currentLengthTextView.text = state.currentPosition
            }
        }

        initClickListeners()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.release()
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
            playerViewModel.playbackControl()
        }

        playerBinding.playerToolbar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
