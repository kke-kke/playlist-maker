package com.example.playlistmaker.data.library

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.library.models.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase, private val playlistConvertor: PlaylistDbConvertor): PlaylistsRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getAllPlaylists().map { list ->
            list.map { playlistConvertor.map(it) }
        }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().insertNewPlaylist(playlistConvertor.map(playlist))
    }

    override suspend fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>) {
        val trackIdsJson = Gson().toJson(newTrackIds)
        val newTrackCount = newTrackIds.size
        appDatabase.getPlaylistDao().updateTrackIdsInPlaylist(playlistId, trackIdsJson, newTrackCount)
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return appDatabase.getPlaylistDao().getPlaylistById(id)?.let { playlistConvertor.map(it) }
    }
}