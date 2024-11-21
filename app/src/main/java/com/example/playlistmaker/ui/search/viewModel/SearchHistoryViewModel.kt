package com.example.playlistmaker.ui.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.settings.api.SearchHistoryInteractor

class SearchHistoryViewModel(private val searchHistoryInteractor: SearchHistoryInteractor): ViewModel() {
    private val _trackHistoryList = MutableLiveData<List<Track>>()
    val trackHistoryList: LiveData<List<Track>> get() = _trackHistoryList

    init {
        loadHistory()
    }

    private fun loadHistory() {
        _trackHistoryList.value = searchHistoryInteractor.loadTrackHistory()
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        loadHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

}