package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var url: String
    private lateinit var play: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        // кнопка "назад"
        setSupportActionBar(playerBinding.playerToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        playerBinding.playerToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // получение трека из предыдущей активити
        val track = intent.getSerializableExtra("TRACK") as Track
        bindTrack(playerBinding, track)

        url = track.previewUrl ?: ""
        if (url.isNotEmpty()) {
            preparePlayer()
        } else {
            Log.e("PlayerActivity", "previewUrl пустой или null.")
        }

        play = playerBinding.playButton
        play.setOnClickListener { playbackControl() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            play.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}