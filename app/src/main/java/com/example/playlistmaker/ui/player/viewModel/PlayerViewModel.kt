package com.example.playlistmaker.ui.player.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.utils.Constants.INITIAL_TRACK_TIME
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerScreenState>().apply {
        value = PlayerScreenState()
    }
    val playerState: LiveData<PlayerScreenState> get() = _playerState

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
            trackLength = mediaPlayerInteractor.getDuration()
            val formattedLength = formattedPosition(trackLength)
            updateState(isPrepared = true, trackLength = formattedLength)
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
        updateState(isPlaying = true, currentPosition = formattedPosition(lastPosition))
        playbackHandler.post(playbackRunnable)
    }

    fun pause() {
        lastPosition = mediaPlayerInteractor.getCurrentPosition()
        mediaPlayerInteractor.pausePlayer()
        updateState(isPlaying = false, currentPosition = formattedPosition(lastPosition))
        playbackHandler.removeCallbacks(playbackRunnable)
    }

    fun release() {
        mediaPlayerInteractor.releasePlayer()
        playbackHandler.removeCallbacks(playbackRunnable)
    }

    private fun updateCurrentPosition() {
        val currentPos = mediaPlayerInteractor.getCurrentPosition().coerceAtMost(trackLength)
        updateState(currentPosition = formattedPosition(currentPos))
    }

    private fun stopPlaybackAtLimit() {
        lastPosition = 0
        pause()
        updateState(isPlaying = false, currentPosition = INITIAL_TRACK_TIME)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.releasePlayer()
        playbackHandler.removeCallbacks(playbackRunnable)
    }

    private fun updateState(isPlaying: Boolean? = null, currentPosition: String? = null, trackLength: String? = null, isPrepared: Boolean? = null) {
        val currentState = _playerState.value ?: PlayerScreenState()
        _playerState.value = currentState.copy(
            isPlaying = isPlaying ?: currentState.isPlaying,
            currentPosition = currentPosition ?: currentState.currentPosition,
            trackLength = trackLength ?: currentState.trackLength,
            isPrepared = isPrepared ?: currentState.isPrepared
        )
    }

    private fun formattedPosition(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }
}
