package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.ui.search.SearchHistory
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.Constants.SETTINGS_PREFERENCES

object Creator {

    private lateinit var applicationContext: App

    fun initialize(app: App) {
        applicationContext = app
    }

    // track
    private fun getTrackRepository(sharedPreferences: SharedPreferences): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), SearchHistory(sharedPreferences))
    }

    fun provideTrackInteractor(sharedPreferences: SharedPreferences): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(sharedPreferences))
    }

    // mediaPlayer
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }

    // settings
    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            applicationContext.getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(), applicationContext)
    }

    // sharing
    fun provideSharingInteractor() : SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }
}