package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository
import com.example.playlistmaker.utils.App

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository, private val app: App): SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        settingsRepository.setDarkThemeEnabled(isDarkTheme)
        app.switchTheme(isDarkTheme)
    }
}