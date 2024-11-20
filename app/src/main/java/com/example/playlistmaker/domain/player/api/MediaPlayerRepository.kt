package com.example.playlistmaker.domain.player.api

interface MediaPlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun goToPosition(position: Int)
    fun getDuration(): Int
}