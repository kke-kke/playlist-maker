package com.example.playlistmaker.data.library

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase, private val playlistConvertor: PlaylistDbConvertor): PlaylistsRepository {

    private val playlistDao = appDatabase.getPlaylistDao()
    private val playlistTrackDao = appDatabase.getPlaylistTrackDao()

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list ->
            list.map { playlistConvertor.map(it) }
        }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertNewPlaylist(playlistConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.id, playlist.name, playlist.description, playlist.coverUri)
    }

    override suspend fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>) {
        val trackIdsJson = Gson().toJson(newTrackIds)
        val newTrackCount = newTrackIds.size
        playlistDao.updateTrackIdsInPlaylist(playlistId, trackIdsJson, newTrackCount)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist?> {
        return playlistDao.getPlaylistById(id).map { entity ->
            entity?.let { playlistConvertor.map(it) }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val isAlreadyInPlaylist = playlistTrackDao.getTrackById(track.trackId, playlist.id) != null
        if (isAlreadyInPlaylist) {
            return false
        }

        playlistTrackDao.insertTrack(playlistConvertor.map(track, playlist.id))
        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(track.trackId) }
        val updatedTrackIdsJson = Gson().toJson(updatedTrackIds)

        playlistDao.updateTrackIdsInPlaylist(playlist.id, updatedTrackIdsJson, updatedTrackIds.size)

        return true
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        return playlistTrackDao.getTracksByIds(trackIds)
            .map { trackEntity -> playlistConvertor.map(trackEntity) }
            .distinctBy { it.trackId }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int, updatedTrackIds: List<Int>) {
        playlistTrackDao.deleteTrackFromPlaylist(trackId, playlistId)

        val newTrackIds = Gson().toJson(updatedTrackIds)
        val newTrackCount = updatedTrackIds.size

        playlistDao.updateTrackIdsInPlaylist(playlistId, newTrackIds, newTrackCount)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistTrackDao.deleteTracksByPlaylistId(playlistId)
        playlistDao.deletePlaylistById(playlistId)
    }
}