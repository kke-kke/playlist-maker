package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.utils.Constants.THEME_KEY

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences): SettingsRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_KEY, isEnabled).apply()
    }

}