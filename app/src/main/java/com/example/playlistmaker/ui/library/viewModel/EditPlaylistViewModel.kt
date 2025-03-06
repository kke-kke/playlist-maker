package com.example.playlistmaker.ui.library.viewModel

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(playlistsInteractor: PlaylistsInteractor, sharingInteractor: SharingInteractor)
    : PlaylistsViewModel(playlistsInteractor, sharingInteractor) {

    fun loadPlaylistData(playlist: Playlist) {
        _playlist.postValue(playlist)
    }

    fun updatePlaylist(name: String, description: String, coverUri: String) {
        viewModelScope.launch {
            val existingPlaylist = _playlist.value ?: return@launch

            val updatedPlaylist = existingPlaylist.copy(
                name = name,
                description = description,
                coverUri = coverUri
            )

            playlistsInteractor.updatePlaylist(updatedPlaylist)
            _playlist.postValue(updatedPlaylist)
        }
    }

}