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

    fun performSearch(query: String) {
        _searchState.value = SearchScreenState.Loading

        viewModelScope.launch {
            interactor
                .searchTracks(query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }

    }

    fun cancelSearch() {
        _searchState.value = SearchScreenState.Empty
    }

    fun resetSearchState() {
        _searchState.value = SearchScreenState.Empty
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                renderState(SearchScreenState.Error(message = errorMessage))
            }
            tracks.isEmpty() -> {
                renderState(SearchScreenState.Empty)
            }
            else -> {
                renderState(SearchScreenState.Success(tracks))
            }
        }
    }

    private fun renderState(state: SearchScreenState) {
        _searchState.postValue(state)
    }
}