package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.models.Resource
import com.example.playlistmaker.domain.search.models.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TrackConsumer)

    fun addToHistory(track: Track)

    fun getHistory(): List<Track>

    fun clearHistory()

    interface TrackConsumer {
        fun consume(foundTracks: Resource<List<Track>>)
    }
}