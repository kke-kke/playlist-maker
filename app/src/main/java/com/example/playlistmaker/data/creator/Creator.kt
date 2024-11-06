package com.example.playlistmaker.data.creator

import android.content.SharedPreferences
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.ui.search.SearchHistory

object Creator {
    private fun getTrackRepository(sharedPreferences: SharedPreferences): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), SearchHistory(sharedPreferences))
    }

    fun provideTrackInteractor(sharedPreferences: SharedPreferences): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(sharedPreferences))
    }
}