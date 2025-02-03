package com.example.playlistmaker.data.library

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylists()
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistsRepository.insertPlaylist(playlist)
    }

    override suspend fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>) {
        playlistsRepository.updateTrackIds(playlistId, newTrackIds)
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return playlistsRepository.getPlaylistById(id)
    }
}