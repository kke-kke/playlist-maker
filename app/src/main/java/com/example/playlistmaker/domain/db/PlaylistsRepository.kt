package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.library.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>)
    suspend fun getPlaylistById(id: Int): Playlist?
}