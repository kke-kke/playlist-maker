package com.example.playlistmaker.ui.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {
    private val _searchState = MutableLiveData<SearchScreenState>()
    val searchState: LiveData<SearchScreenState> get() = _searchState

    private val searchQuery = MutableStateFlow("")

    private val searchResults = MutableLiveData<List<Track>>()

    private var lastQuery: String? = null
    private var lastSearchResults: List<Track>? = null

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(2000)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchTextChanged(query: String) {
        searchQuery.value = query
    }

    fun restoreSearchState(): Pair<String?, List<Track>?> {
        return Pair(lastQuery, lastSearchResults)
    }

    fun saveSearchState(query: String, results: List<Track>) {
        lastQuery = query
        lastSearchResults = results
    }

    fun performSearch(query: String) {
        _searchState.value = SearchScreenState.Loading

        viewModelScope.launch {
            interactor
                .searchTracks(query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                    saveSearchState(query, pair.first ?: emptyList())
                }
        }
    }

    fun resetSearchState() {
        _searchState.value = SearchScreenState.Empty
        lastQuery = null
        lastSearchResults = null
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                renderState(SearchScreenState.Error(message = errorMessage))
            }
            foundTracks.isNullOrEmpty() -> {
                renderState(SearchScreenState.Empty)
            }
            else -> {
                searchResults.value = foundTracks ?: emptyList()
                renderState(SearchScreenState.Success(foundTracks))
            }
        }
    }

    private fun renderState(state: SearchScreenState) {
        _searchState.postValue(state)
    }
}