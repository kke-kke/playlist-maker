package com.example.playlistmaker.data.library

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.TrackDbConvertor
import com.example.playlistmaker.domain.db.FavouritesRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavouritesRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor) : FavouritesRepository {
    override fun isTrackFavourite(trackId: Int): Flow<Boolean> {
        return appDatabase.getTrackDao().isTrackFavourite(trackId)
            .map { it > 0 }

    }

    override suspend fun addTrackToFavourites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.getTrackDao().insertNewTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavourites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.getTrackDao().deleteTrackEntity(trackEntity)
    }

    override fun favouriteTracks(): Flow<List<Track>> {
        return appDatabase.getTrackDao().getTracks()
            .map { trackEntities ->
                trackEntities.map { trackEntity -> trackDbConvertor.map(trackEntity) }
            }
    }

}