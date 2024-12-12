package com.example.playlistmaker.ui.library.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.api.TrackInteractor

class FavouritesViewModel(private val trackId: String, private val trackInteractor: TrackInteractor) : ViewModel() {
    private val _favouritesLiveData = MutableLiveData<FavouritesScreenState>()
    fun observeState(): LiveData<FavouritesScreenState> = _favouritesLiveData

    init {

    }
}