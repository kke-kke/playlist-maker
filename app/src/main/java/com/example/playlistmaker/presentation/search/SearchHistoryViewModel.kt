package com.example.playlistmaker.presentation.search

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchHistory

class SearchHistoryViewModel(private val sharedPreferences: SharedPreferences): ViewModel() {
    private val _trackHistoryList = MutableLiveData<List<Track>>()
    val trackHistoryList: LiveData<List<Track>> get() = _trackHistoryList

    private val searchHistory = SearchHistory(sharedPreferences)

    init {
        loadHistory()
    }

    private fun loadHistory() {
        _trackHistoryList.value = searchHistory.loadTrackHistory()
    }

    fun addTrackToHistory(track: Track) {
        searchHistory.addTrackToHistory(track)
        loadHistory()
    }

    fun clearHistory() {
        searchHistory.clearHistory()
        _trackHistoryList.value = emptyList()
    }

}