package com.example.playlistmaker.data.library

import com.example.playlistmaker.domain.db.FavouritesRepository
import com.example.playlistmaker.domain.library.api.FavouritesInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(private val favouritesRepository: FavouritesRepository) : FavouritesInteractor {
    override fun isTrackFavourite(trackId: Int): Flow<Boolean> {
        return favouritesRepository.isTrackFavourite(trackId)
    }

    override suspend fun addTrackToFavourites(track: Track) {
        favouritesRepository.addTrackToFavourites(track)
    }

    override suspend fun removeTrackFromFavourites(track: Track) {
        favouritesRepository.removeTrackFromFavourites(track)
    }

    override fun getAllFavouriteTracks(): Flow<List<Track>> {
        return favouritesRepository.favouriteTracks()
    }
}
