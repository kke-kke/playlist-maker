package com.example.playlistmaker.domain.api.track

import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

    fun addTrackToHistory(track: Track)

    fun loadTrackHistory(): List<Track>

    fun clearHistory()

}