package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
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