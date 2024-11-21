package com.example.playlistmaker.ui.settings.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.api.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor, private val sharingInteractor: SharingInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    init {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun switchTheme(isEnabled: Boolean) {
        settingsInteractor.switchTheme(isEnabled)
        _isDarkThemeEnabled.value = isEnabled
    }

    fun shareApp(context: Context) {
        sharingInteractor.shareApp(context)
    }

    fun contactSupport(context: Context) {
        sharingInteractor.openSupport(context)
    }

    fun openUserAgreement(context: Context) {
        sharingInteractor.openTerms(context)
    }
}
