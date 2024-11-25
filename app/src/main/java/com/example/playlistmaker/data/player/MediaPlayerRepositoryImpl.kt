package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private var mediaPlayer: MediaPlayer): MediaPlayerRepository {
    private var currentTrack: String? = null
    private var lastPosition = 0

    override fun preparePlayer(url: String, onPrepared: () -> Unit) {
        if (currentTrack != url) {
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
            mediaPlayer.seekTo(lastPosition)
            onPrepared()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        lastPosition = mediaPlayer.currentPosition
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        lastPosition = mediaPlayer.currentPosition
        mediaPlayer.stop()
        mediaPlayer.reset()
        currentTrack = null
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun goToPosition(position: Int) {
        mediaPlayer.seekTo(position)
        lastPosition = position
    }

    override fun getDuration(): Int = mediaPlayer.duration
}