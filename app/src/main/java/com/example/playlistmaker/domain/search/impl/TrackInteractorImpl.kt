package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.search.models.Resource
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getHistory(): List<Track> {
        return repository.loadTrackHistory()
    }

    override fun clearHistory() {
        return repository.clearHistory()
    }
}