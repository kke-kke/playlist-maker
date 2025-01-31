package com.example.playlistmaker.di

import com.example.playlistmaker.ui.library.viewModel.FavouritesViewModel
import com.example.playlistmaker.ui.library.viewModel.PlaylistsViewModel
import com.example.playlistmaker.ui.player.viewModel.PlayerViewModel
import com.example.playlistmaker.ui.search.viewModel.SearchHistoryViewModel
import com.example.playlistmaker.ui.search.viewModel.SearchViewModel
import com.example.playlistmaker.ui.settings.viewModel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SearchHistoryViewModel(get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavouritesViewModel(get())
    }

    viewModel {(playlistId: String) ->
        PlaylistsViewModel(playlistId)
    }
}