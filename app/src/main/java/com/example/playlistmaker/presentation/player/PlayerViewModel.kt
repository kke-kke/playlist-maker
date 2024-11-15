package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.search.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerState>().apply { value = PlayerState.Default }
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _currentPosition = MutableLiveData<String>().apply { value = "00:00" }
    val currentPosition: LiveData<String> get() = _currentPosition

    private val playbackHandler = Handler(Looper.getMainLooper())
    private val playbackRunnable = object : Runnable {
        override fun run() {
            updateCurrentPosition()
            if (mediaPlayerInteractor.isPlaying() && (mediaPlayerInteractor.getCurrentPosition() < trackLength)) {
                playbackHandler.postDelayed(this, 400L)
            } else {
                stopPlaybackAtLimit()
            }
        }
    }

    private var lastPosition: Int = 0
    private var trackLength = 0

    fun loadTrack(track: Track) {
        mediaPlayerInteractor.preparePlayer(track.previewUrl ?: "") {
            _playerState.value = PlayerState.Prepared
            trackLength = mediaPlayerInteractor.getDuration()
        }
    }

    fun playbackControl() {
        if (mediaPlayerInteractor.isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        mediaPlayerInteractor.goToPosition(lastPosition)
        mediaPlayerInteractor.startPlayer()
        _playerState.value = PlayerState.Playing
        playbackHandler.post(playbackRunnable)
    }

    fun pause() {
        lastPosition = mediaPlayerInteractor.getCurrentPosition()
        mediaPlayerInteractor.pausePlayer()
        _playerState.value = PlayerState.Paused
        playbackHandler.removeCallbacks(playbackRunnable)
    }

    fun release() {
        mediaPlayerInteractor.releasePlayer()
        playbackHandler.removeCallbacks(playbackRunnable)
    }

    private fun updateCurrentPosition() {
        val currentPos = mediaPlayerInteractor.getCurrentPosition().coerceAtMost(trackLength)
        val formattedPosition = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPos)
        _currentPosition.value = formattedPosition
    }

    private fun stopPlaybackAtLimit() {
        pause()
        _currentPosition.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackLength)
        _playerState.value = PlayerState.Prepared
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.releasePlayer()
        playbackHandler.removeCallbacks(playbackRunnable)
    }
}
