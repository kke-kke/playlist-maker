package com.example.playlistmaker.domain.library.api

import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>)
    suspend fun getPlaylistById(id: Int): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun getPlaylistDuration(trackIds: List<Int>): Long
    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int, updatedTrackIds: List<Int>)
}