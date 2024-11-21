package com.example.playlistmaker.domain.settings.api

import com.example.playlistmaker.domain.search.models.Track

interface SearchHistoryInteractor {
    fun addTrackToHistory(newTrack: Track)

    fun loadTrackHistory(): List<Track>

    fun clearHistory()
}