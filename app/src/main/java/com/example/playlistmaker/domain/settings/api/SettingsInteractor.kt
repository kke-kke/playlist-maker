package com.example.playlistmaker.domain.settings.api

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isDarkTheme: Boolean)
}