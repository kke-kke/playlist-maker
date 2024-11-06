package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TrackConsumer)

    fun addToHistory(track: Track)

    fun getHistory(): List<Track>

    fun clearHistory()

    interface TrackConsumer {
        fun consume(foundTracks: Resource<List<Track>>)
    }
}