package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    fun isTrackFavourite(trackId: Int): Flow<Boolean>
    suspend fun addTrackToFavourites(track: Track)
    suspend fun removeTrackFromFavourites(track: Track)
    fun favouriteTracks(): Flow<List<Track>>
}