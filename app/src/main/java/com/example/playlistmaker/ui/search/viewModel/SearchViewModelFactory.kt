package com.example.playlistmaker.ui.search.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class SearchViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val interactor = Creator.provideTrackInteractor(sharedPreferences)
        return when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(interactor) as T
            }
            modelClass.isAssignableFrom(SearchHistoryViewModel::class.java) -> {
                SearchHistoryViewModel(sharedPreferences) as T
            }
            else -> throw IllegalArgumentException("Неизвестный ViewModel: ${modelClass.name}")
        }
    }
}