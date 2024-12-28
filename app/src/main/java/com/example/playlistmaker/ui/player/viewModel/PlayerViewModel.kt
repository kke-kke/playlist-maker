package com.example.playlistmaker.ui.player.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.utils.Constants.INITIAL_TRACK_TIME
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {
    private val _playerState = MutableLiveData<PlayerScreenState>().apply {
        value = PlayerScreenState()
    }
    val playerState: LiveData<PlayerScreenState> get() = _playerState

    private var lastPosition: Int = 0
    private var trackLength = 0
    private var progressJob: Job? = null

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
        startProgressUpdates()
    }

    fun pause() {
        lastPosition = mediaPlayerInteractor.getCurrentPosition()
        mediaPlayerInteractor.pausePlayer()
        updateState(isPlaying = false, currentPosition = formattedPosition(lastPosition))
        stopProgressUpdates()
    }

    fun release() {
        mediaPlayerInteractor.releasePlayer()
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressJob = viewModelScope.launch {
            while (mediaPlayerInteractor.isPlaying() && mediaPlayerInteractor.getCurrentPosition() < trackLength) {
                val currentPosition = mediaPlayerInteractor.getCurrentPosition().coerceAtMost(trackLength)
                updateState(currentPosition = formattedPosition(currentPosition))
                delay(400L)
            }
            stopPlaybackAtLimit()
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
    }

    private fun stopPlaybackAtLimit() {
        lastPosition = 0
        mediaPlayerInteractor.pausePlayer()
        stopProgressUpdates()
        updateState(isPlaying = false, currentPosition = INITIAL_TRACK_TIME)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.releasePlayer()
        stopProgressUpdates()
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
