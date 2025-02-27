package com.example.playlistmaker.data.library

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

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

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistDuration(trackIds: List<Int>): Long {
        val tracks = playlistsRepository.getTracksByIds(trackIds)
        val durationSumMillis = tracks.sumOf { it.trackTime }

        val totalMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(durationSumMillis).toLong()

        return totalMinutes
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        return playlistsRepository.getTracksByIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int, updatedTrackIds: List<Int>) {
        return playlistsRepository.removeTrackFromPlaylist(trackId, playlistId, updatedTrackIds)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }
}