package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.player.api.MediaPlayerRepository
import com.example.playlistmaker.domain.search.api.TrackInteractor
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.settings.api.SearchHistoryRepository
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.Constants.SEARCH_HISTORY
import com.example.playlistmaker.utils.Constants.SETTINGS_PREFERENCES

object Creator {

    private lateinit var applicationContext: App

    fun initialize(app: App) {
        applicationContext = app
    }

    // search history
    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(applicationContext.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE))
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    // track
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(), getSearchHistoryRepository())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    // mediaPlayer
    private fun getMediaPlayerRepository():MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
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