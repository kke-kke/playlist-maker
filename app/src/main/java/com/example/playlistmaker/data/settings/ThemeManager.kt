package com.example.playlistmaker.data.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.utils.Constants.THEME_KEY

class ThemeManager(private val sharedPreferences: SharedPreferences) {
    fun switchTheme(darkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}