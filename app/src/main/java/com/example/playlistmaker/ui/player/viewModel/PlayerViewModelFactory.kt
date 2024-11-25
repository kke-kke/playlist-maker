package com.example.playlistmaker.ui.player.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class PlayerViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val interactor = Creator.provideMediaPlayerInteractor()
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(interactor) as T
        }
        throw IllegalArgumentException("Неизвестный ViewModel")
    }
}