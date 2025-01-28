package com.example.playlistmaker.data.library

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.TrackDbConvertor
import com.example.playlistmaker.domain.db.FavouritesRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor) : FavouritesRepository {
    override fun isTrackFavourite(trackId: Int): Flow<Boolean> {
        return appDatabase.getTrackDao().isTrackFavourite(trackId)
            .map { it > 0 }

    }

    override suspend fun addTrackToFavourites(track: Track) {
        val dateSaved = System.currentTimeMillis()
        val trackEntity = trackDbConvertor.map(track, dateSaved)
        appDatabase.getTrackDao().insertNewTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavourites(track: Track) {
        appDatabase.getTrackDao().deleteTrackById(track.trackId)
    }

    override fun favouriteTracks(): Flow<List<Track>> {
        return appDatabase.getTrackDao().getTracks()
            .map { trackEntities ->
                trackEntities.map { trackEntity -> trackDbConvertor.map(trackEntity) }
            }
    }

}