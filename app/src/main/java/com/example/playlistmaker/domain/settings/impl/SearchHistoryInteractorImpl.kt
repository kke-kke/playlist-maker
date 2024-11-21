package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.settings.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.settings.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {
    override fun addTrackToHistory(newTrack: Track) {
        repository.addTrackToHistory(newTrack)
    }

    override fun loadTrackHistory(): List<Track> {
        return repository.loadTrackHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}