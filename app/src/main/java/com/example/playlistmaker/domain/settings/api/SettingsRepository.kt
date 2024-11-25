package com.example.playlistmaker.domain.settings.api

interface SettingsRepository {

    fun isDarkThemeEnabled(): Boolean

    fun setDarkThemeEnabled(isEnabled: Boolean)
}