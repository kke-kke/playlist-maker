package com.example.playlistmaker.ui.search.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.models.Resource
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

        interactor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: Resource<List<Track>>) {
                when (foundTracks) {
                    is Resource.Success -> {
                        val tracks = foundTracks.data
                        if (tracks.isEmpty()) {
                            _searchState.postValue(SearchScreenState.Empty)
                        } else {
                            _searchState.postValue(SearchScreenState.Success(tracks))
                        }
                    }
                    is Resource.Error -> {
                        _searchState.postValue(
                            SearchScreenState.Error(
                                foundTracks.message ?: "Unknown error"
                            )
                        )
                    }
                }
            }
        })

    }

    fun cancelSearch() {
        _searchState.value = SearchScreenState.Empty
    }

    fun resetSearchState() {
        _searchState.value = SearchScreenState.Empty
    }
}