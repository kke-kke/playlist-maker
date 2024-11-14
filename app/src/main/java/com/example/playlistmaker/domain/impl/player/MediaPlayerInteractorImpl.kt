package com.example.playlistmaker.domain.impl.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.player.MediaPlayerInteractor

class MediaPlayerInteractorImpl: MediaPlayerInteractor {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: String? = null
    private var lastPosition: Int = 0

    override fun preparePlayer(url: String, onPrepared: () -> Unit) {
        if (mediaPlayer == null || currentTrack != url) {
            releasePlayer()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    if (lastPosition > 0) seekTo(lastPosition)
                    onPrepared()
                }
            }
            currentTrack = url
        } else {
            mediaPlayer?.seekTo(lastPosition)
            onPrepared()
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
    }

    override fun pausePlayer() {
        lastPosition = mediaPlayer?.currentPosition ?: 0
        mediaPlayer?.pause()
    }

    override fun releasePlayer() {
        lastPosition = mediaPlayer?.currentPosition ?: lastPosition
        mediaPlayer?.release()
        mediaPlayer = null
        currentTrack = null
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: lastPosition

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun goToPosition(position: Int) {
        mediaPlayer?.seekTo(position)
        lastPosition = position
    }

    override fun getDuration(): Int = mediaPlayer?.duration ?: 0
}