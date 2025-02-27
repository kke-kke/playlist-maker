package com.example.playlistmaker.ui.library.viewModel;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel(){
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist

    private val _addTrackStatus = MutableLiveData<AddTrackState?>()
    val addTrackStatus: LiveData<AddTrackState?> = _addTrackStatus

    private val _playlistDuration = MutableLiveData<Long>()
    val playlistDuration: LiveData<Long> get() = _playlistDuration

    private val _tracksMap = mutableMapOf<Int, MutableLiveData<List<Track>>>()

    fun getTracksLiveData(playlistId: Int): LiveData<List<Track>> {
        return _tracksMap.getOrPut(playlistId) { MutableLiveData() }
    }

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists()
                .collect { playlists ->
                    _playlists.postValue(playlists)
                }
        }
    }

    fun loadTracks(playlistId: Int, trackIds: List<Int>) {
        viewModelScope.launch {
            val tracks = playlistsInteractor.getTracksByIds(trackIds)
            _tracksMap.getOrPut(playlistId) { MutableLiveData() }.apply {
                postValue(emptyList())
                postValue(tracks)
            }
        }
    }

    fun removeTrack(track: Track, playlistId: Int) {
        viewModelScope.launch {
            val updatedTrackIds = playlistsInteractor.getPlaylistById(playlistId)?.trackIds?.toMutableList()
                ?.apply { remove(track.trackId) } ?: return@launch

            playlistsInteractor.removeTrackFromPlaylist(track.trackId, playlistId, updatedTrackIds)

            loadPlaylists()
            loadTracks(playlistId, updatedTrackIds)
        }
    }

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(playlist)
            loadPlaylists()
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val isAdded = playlistsInteractor.addTrackToPlaylist(track, playlist)
            if (isAdded) {
                _addTrackStatus.postValue(AddTrackState.Success(playlist.name))

                playlistsInteractor.getAllPlaylists().collect { playlists ->
                    _playlists.postValue(playlists)

                    playlists.forEach { updatedPlaylist ->
                        loadTracks(updatedPlaylist.id, updatedPlaylist.trackIds)
                    }
                }
            } else {
                _addTrackStatus.postValue(AddTrackState.Error("Трек уже добавлен в плейлист ${playlist.name}"))
            }
        }
    }

    fun clearAddTrackStatus() {
        _addTrackStatus.value = null
    }

    fun calculatePlaylistDuration(trackIds: List<Int>) {
        viewModelScope.launch {
            val duration = playlistsInteractor.getPlaylistDuration(trackIds)
            _playlistDuration.postValue(duration)
        }
    }
}
