package com.example.playlistmaker.ui.search

import android.content.SharedPreferences
import com.example.playlistmaker.utils.Constants.SEARCH_HISTORY
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun saveTrackHistory(tracks: List<Track>) {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY, Gson().toJson(tracks))
            .apply()
    }

    fun loadTrackHistory(): ArrayList<Track> {
        val tracksJson = sharedPreferences.getString(SEARCH_HISTORY, null)
        return if (tracksJson != null) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            Gson().fromJson(tracksJson, type) ?: arrayListOf()
        } else {
            arrayListOf()
        }
    }

    fun addTrackToHistory(newTrack: Track) {
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

    fun clearHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }

}