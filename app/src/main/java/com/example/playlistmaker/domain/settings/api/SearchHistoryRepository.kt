package com.example.playlistmaker.domain.settings.api

import com.example.playlistmaker.domain.search.models.Track

interface SearchHistoryRepository {

    fun saveTrackHistory(tracks: List<Track>)

    fun loadTrackHistory(): ArrayList<Track>

    fun addTrackToHistory(newTrack: Track)

    fun clearHistory()
}