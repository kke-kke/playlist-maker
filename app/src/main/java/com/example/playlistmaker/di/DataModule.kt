package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.network.SearchApi
import com.example.playlistmaker.data.settings.ThemeManager
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.utils.Constants.SEARCH_HISTORY
import com.example.playlistmaker.utils.Constants.SETTINGS_PREFERENCES
import com.example.playlistmaker.utils.Constants.THEME_KEY
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        MediaPlayer()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single(named(THEME_KEY)) {
        androidContext().getSharedPreferences(THEME_KEY, Context.MODE_PRIVATE)
    }

    single(named(SETTINGS_PREFERENCES)) {
        androidContext().getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE)
    }

    single(named(SEARCH_HISTORY)) {
        androidContext().getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    }

    single {
        ThemeManager(get(named(SETTINGS_PREFERENCES)))
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    single<SearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        Gson()
    }
}
