package com.example.playlistmaker.ui.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val trackInteractor = Creator.provideTrackInteractor()
        val historyInteractor = Creator.provideSearchHistoryInteractor()
        return when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(trackInteractor) as T
            }
            modelClass.isAssignableFrom(SearchHistoryViewModel::class.java) -> {
                SearchHistoryViewModel(historyInteractor) as T
            }
            else -> throw IllegalArgumentException("Неизвестный ViewModel: ${modelClass.name}")
        }
    }
}