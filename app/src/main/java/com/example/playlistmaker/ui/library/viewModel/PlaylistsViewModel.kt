package com.example.playlistmaker.ui.library.viewModel;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel(){
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist

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

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(playlist)
            loadPlaylists()
        }
    }

    fun updateTrackIds(playlistId: Int, newTrackIds: List<Int>) {
        viewModelScope.launch {
            playlistsInteractor.updateTrackIds(playlistId, newTrackIds)
            loadPlaylists()
        }
    }

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            val result = playlistsInteractor.getPlaylistById(id)
            _playlist.postValue(result)
        }
    }
}
