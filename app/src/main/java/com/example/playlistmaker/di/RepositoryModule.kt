package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.converters.TrackDbConvertor
import com.example.playlistmaker.data.library.FavouritesRepositoryImpl
import com.example.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.search.TrackRepositoryImpl
import com.example.playlistmaker.data.search.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.domain.db.FavouritesRepository
import com.example.playlistmaker.domain.player.api.MediaPlayerRepository
import com.example.playlistmaker.domain.search.api.TrackRepository
import com.example.playlistmaker.domain.settings.api.SearchHistoryRepository
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.utils.Constants.SEARCH_HISTORY
import com.example.playlistmaker.utils.Constants.THEME_KEY
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }
    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(named(SEARCH_HISTORY)))
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named(THEME_KEY)))
    }

    factory { TrackDbConvertor() }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }
}
