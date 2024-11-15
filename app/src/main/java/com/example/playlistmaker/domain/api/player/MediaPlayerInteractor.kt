package com.example.playlistmaker.domain.api.player

interface MediaPlayerInteractor {

    fun preparePlayer(url: String, onPrepared: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun getCurrentPosition(): Int

    fun isPlaying(): Boolean

    fun goToPosition(position: Int)

    fun getDuration(): Int
}