package com.example.playlistmaker.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: TrackInteractor) : ViewModel() {
    val searchResults = MutableLiveData<List<Track>>()
    private val _trackList = MutableLiveData<Resource<List<Track>>>()
    val trackList: LiveData<Resource<List<Track>>> get() = _trackList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val searchQuery = MutableStateFlow("")

    private val _emptyState = MutableLiveData<Boolean>()
    val emptyState: LiveData<Boolean> get() = _emptyState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

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
        _isLoading.value = true
        _emptyState.value = false
        _errorState.value = null

        interactor.searchTracks(query, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: Resource<List<Track>>) {
                _isLoading.postValue(false)

                when (foundTracks) {
                    is Resource.Success -> {
                        val tracks = foundTracks.data
                        if (tracks.isEmpty()) {
                            _emptyState.postValue(true)
                            _trackList.postValue(Resource.Success(emptyList()))
                        } else {
                            _emptyState.postValue(false)
                            _trackList.postValue(Resource.Success(tracks))
                        }
                    }
                    is Resource.Error -> {
                        _errorState.postValue(foundTracks.message)
                        _trackList.postValue(Resource.Error(foundTracks.message))
                    }
                }
            }
        })

    }
}