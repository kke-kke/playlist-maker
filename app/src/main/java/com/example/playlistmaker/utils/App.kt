package com.example.playlistmaker.utils

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.utils.Constants.SETTINGS_PREFERENCES
import com.example.playlistmaker.utils.Constants.THEME_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(repositoryModule, dataModule, viewModelModule, interactorModule)
        }
        applyThemeFromPreferences()
    }

    private fun applyThemeFromPreferences() {
        val sharedPreferences = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
        val isFirstLaunch = !sharedPreferences.contains(THEME_KEY)

        val isDarkThemeEnabled = if (isFirstLaunch) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isSystemDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean(THEME_KEY, isSystemDarkMode).apply()
            isSystemDarkMode
        } else {
            sharedPreferences.getBoolean(THEME_KEY, false)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

}