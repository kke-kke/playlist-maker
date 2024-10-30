package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Constants.SETTINGS_PREFERENCES
import com.example.playlistmaker.Constants.THEME_KEY

class App : Application() {

    override fun onCreate() {
        super.onCreate()
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

    fun switchTheme(darkTheme: Boolean) {
        getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE).edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}