package com.example.playlistmaker.ui.library.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.library.api.FavouritesInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favouritesInteractor: FavouritesInteractor) : ViewModel() {

    private val _favouriteTracks = MutableLiveData<List<Track>>()
    val favouriteTracks: LiveData<List<Track>> get() = _favouriteTracks

    private val _isTrackFavourite = MutableStateFlow(false)
    val isTrackFavourite: StateFlow<Boolean> = _isTrackFavourite

    init {
        viewModelScope.launch {
            favouritesInteractor.getAllFavouriteTracks()
                .collect { tracks ->
                    _favouriteTracks.postValue(tracks)
                }
        }
    }

    fun isTrackFavourite(trackId: Int) {
        viewModelScope.launch {
            favouritesInteractor.isTrackFavourite(trackId).collect { isFavourite ->
                _isTrackFavourite.value = isFavourite
            }
        }
    }

    fun addTrackToFavourites(track: Track) {
        viewModelScope.launch {
            favouritesInteractor.addTrackToFavourites(track)
            isTrackFavourite(track.trackId)
        }
    }

    fun removeTrackFromFavourites(track: Track) {
        viewModelScope.launch {
            favouritesInteractor.removeTrackFromFavourites(track)
            isTrackFavourite(track.trackId)
        }
    }
}