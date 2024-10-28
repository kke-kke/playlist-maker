package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var url: String
    private lateinit var play: ImageButton
    private lateinit var currentTimestamp: TextView
    private val playbackHandler = Handler(Looper.getMainLooper())
    private var playbackRunnable = Runnable { getCurrentPlayback() }

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

        // textView текущего момента воспроизведения
        currentTimestamp = playerBinding.currentLengthTextView
        currentTimestamp.text = DEFAULT_PLAYBACK

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
        playbackHandler.removeCallbacks(playbackRunnable)
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
            currentTimestamp.text = DEFAULT_PLAYBACK
            playerState = STATE_PREPARED
            playbackHandler.removeCallbacks(playbackRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        playbackHandler.post(playbackRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playbackHandler.removeCallbacks(playbackRunnable)
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

    private fun getCurrentPlayback() {
        if (playerState == STATE_PLAYING) {
            currentTimestamp.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            playbackHandler.postDelayed(playbackRunnable, HANDLER_WAIT)
        } else {
            currentTimestamp.text = DEFAULT_PLAYBACK
        }

    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val HANDLER_WAIT = 400L
        private const val DEFAULT_PLAYBACK = "00:00"
    }

}