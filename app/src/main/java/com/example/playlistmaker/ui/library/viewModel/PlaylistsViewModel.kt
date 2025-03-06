package com.example.playlistmaker.ui.library.viewModel;

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.library.api.PlaylistsInteractor
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.sharing.SharingInteractor
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

open class PlaylistsViewModel(val playlistsInteractor: PlaylistsInteractor, private val sharingInteractor: SharingInteractor) : ViewModel(){
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist

    private val _addTrackStatus = MutableLiveData<AddTrackState?>()
    val addTrackStatus: LiveData<AddTrackState?> = _addTrackStatus

    private val _playlistDuration = MutableLiveData<Long>()
    val playlistDuration: LiveData<Long> get() = _playlistDuration

    private val _tracksMap = mutableMapOf<Int, MutableLiveData<List<Track>>>()

    fun getTracksLiveData(playlistId: Int): LiveData<List<Track>> {
        return _tracksMap.getOrPut(playlistId) { MutableLiveData() }
    }

    private val _deletionState = MutableLiveData<Boolean>()
    val deletionState: LiveData<Boolean> get() = _deletionState

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
                postValue(tracks)
            }
        }
    }

    fun removeTrack(track: Track, playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId)
                .firstOrNull()?.let { playlist ->
                    val updatedTrackIds = playlist.trackIds.toMutableList().apply { remove(track.trackId) }

                    playlistsInteractor.removeTrackFromPlaylist(track.trackId, playlistId, updatedTrackIds)


                    _tracksMap[playlistId]?.postValue(playlistsInteractor.getTracksByIds(updatedTrackIds))
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

    fun sharePlaylist(context: Context, playlist: Playlist) {
        viewModelScope.launch{
            val tracks = playlistsInteractor.getTracksByIds(playlist.trackIds)
            val trackList = tracks.mapIndexed { index, track ->
                "${index + 1}. ${track.artistName} - ${track.trackName} (${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)})"
            }
            sharingInteractor.sharePlaylist(
                context,
                playlist.name,
                playlist.description,
                context.resources.getQuantityString(R.plurals.tracks_count, playlist.trackCount, playlist.trackCount),
                trackList
            )
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            try {
                playlistsInteractor.deletePlaylist(playlistId)
                _deletionState.postValue(true)
            } catch (e: Exception) {
                _deletionState.postValue(false)
            }
        }
    }
}
