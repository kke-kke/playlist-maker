package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.player.api.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository): MediaPlayerInteractor {

    override fun preparePlayer(url: String, onPrepared: () -> Unit) {
        mediaPlayerRepository.preparePlayer(url, onPrepared)
    }

    override fun startPlayer() {
        mediaPlayerRepository.startPlayer()
    }

    override fun pausePlayer() {
        mediaPlayerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        mediaPlayerRepository.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayerRepository.isPlaying()
    }

    override fun goToPosition(position: Int) {
        mediaPlayerRepository.goToPosition(position)
    }

    override fun getDuration(): Int {
        return mediaPlayerRepository.getDuration()
    }
}