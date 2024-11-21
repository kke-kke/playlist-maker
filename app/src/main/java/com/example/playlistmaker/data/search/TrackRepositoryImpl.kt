package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.search.models.Resource
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.settings.api.SearchHistoryRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient, private val searchHistoryRepository: SearchHistoryRepository) : TrackRepository {

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
        searchHistoryRepository.addTrackToHistory(track)
    }

    override fun loadTrackHistory(): List<Track> {
        return searchHistoryRepository.loadTrackHistory()
    }

    override fun clearHistory() {
        return searchHistoryRepository.clearHistory()
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