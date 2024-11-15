package com.example.playlistmaker.domain.api.settings

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isDarkTheme: Boolean)
}