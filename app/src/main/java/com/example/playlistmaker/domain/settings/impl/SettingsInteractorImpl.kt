package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.ThemeManager
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository, private val themeManager: ThemeManager): SettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean {
        return settingsRepository.isDarkThemeEnabled()
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        settingsRepository.setDarkThemeEnabled(isDarkTheme)
        themeManager.switchTheme(isDarkTheme)
    }
}