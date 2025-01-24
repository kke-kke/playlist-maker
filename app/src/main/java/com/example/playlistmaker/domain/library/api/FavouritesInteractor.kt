package com.example.playlistmaker.domain.library.api

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    fun isTrackFavourite(trackId: Int): Flow<Boolean>
    suspend fun addTrackToFavourites(track: Track)
    suspend fun removeTrackFromFavourites(track: Track)
    fun getAllFavouriteTracks(): Flow<List<Track>>
}