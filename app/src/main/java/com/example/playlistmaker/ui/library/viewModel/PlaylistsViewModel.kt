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

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getAllPlaylists()
                .collect { playlists ->
                    _playlists.postValue(playlists)
                }
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
                loadPlaylists()
            } else {
                _addTrackStatus.postValue(AddTrackState.Error("Трек уже добавлен в плейлист ${playlist.name}"))
            }
        }
    }

    fun clearAddTrackStatus() {
        _addTrackStatus.value = null
    }
}
