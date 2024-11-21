package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.settings.api.SearchHistoryRepository
import com.example.playlistmaker.utils.Constants.SEARCH_HISTORY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences): SearchHistoryRepository {

    override fun saveTrackHistory(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(tracks))
            .apply()
    }

    override fun loadTrackHistory(): ArrayList<Track> {
        val tracksJson = sharedPreferences.getString(SEARCH_HISTORY, null)
        return if (tracksJson != null) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            Gson().fromJson(tracksJson, type) ?: arrayListOf()
        } else {
            arrayListOf()
        }
    }

    override fun addTrackToHistory(newTrack: Track) {
        val trackHistoryList = loadTrackHistory()

        val existingTrackIndex = trackHistoryList.indexOfFirst { it.trackId == newTrack.trackId }

        // если трек существует, удаляем его из списка
        if (existingTrackIndex != -1) {
            trackHistoryList.removeAt(existingTrackIndex)
        }

        trackHistoryList.add(0, newTrack)

        // если список больше 10, удаляем последний элемент
        if (trackHistoryList.size > MAX_HISTORY_SIZE) {
            trackHistoryList.removeAt(trackHistoryList.size - 1)
        }

        saveTrackHistory(trackHistoryList)
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }

}