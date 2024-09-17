package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(SWITCH_STATE, DEFAULT_VALUE)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(SWITCH_STATE, darkThemeEnabled).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val SWITCH_STATE: String = "SWITCH_STATE"
        private const val DEFAULT_VALUE = false
        private const val PREFERENCES = "My_Preferences"
    }
}