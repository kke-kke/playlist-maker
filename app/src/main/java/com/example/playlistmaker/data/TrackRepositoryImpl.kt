package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchHistory

class TrackRepositoryImpl(private val networkClient: NetworkClient, private val searchHistory: SearchHistory) : TrackRepository {
    private val trackHistory = mutableListOf<Track>()

    override fun searchTracks(expression: String): Resource<List<Track>> {
        return try {
            val response = networkClient.doRequest(TrackSearchRequest(expression))
            if (response.resultCode == 200) {
                val results = (response as TrackSearchResponse).results.map { it.toDomainModel() }
                Resource.Success(results)
            } else {
                Resource.Error("Server error: ${response.resultCode}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun addTrackToHistory(track: Track) {
        searchHistory.addTrackToHistory(track)
    }

    override fun loadTrackHistory(): List<Track> {
        return searchHistory.loadTrackHistory()
    }

    override fun clearHistory() {
        return searchHistory.clearHistory()
    }

    private fun TrackDto.toDomainModel(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"),
            previewUrl = previewUrl,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country
        )
    }
}