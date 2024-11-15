package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.ui.search.SearchHistory
import com.example.playlistmaker.utils.App

object Creator {

    private lateinit var applicationContext: App

    fun initialize(app: App) {
        applicationContext = app
    }

    private fun getTrackRepository(sharedPreferences: SharedPreferences): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), SearchHistory(sharedPreferences))
    }

    fun provideTrackInteractor(sharedPreferences: SharedPreferences): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(sharedPreferences))
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(applicationContext)
    }

    fun provideSharingInteractor() : SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }
}