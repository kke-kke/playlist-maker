package com.example.playlistmaker.ui.library.viewModel;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(private val playlistId: String) : ViewModel(){
    private val _playlistsLiveData = MutableLiveData<PlaylistsScreenState>()
    fun observeState(): LiveData<PlaylistsScreenState> = _playlistsLiveData

    init {

    }
}
