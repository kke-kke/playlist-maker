package com.example.playlistmaker.domain.impl.settings

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.domain.api.settings.SettingsInteractor
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.Constants.SETTINGS_PREFERENCES
import com.example.playlistmaker.utils.Constants.THEME_KEY

class SettingsInteractorImpl(private val app: App): SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = app.getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        app.switchTheme(isDarkTheme)
    }
}