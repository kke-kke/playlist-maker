package com.example.playlistmaker.ui.settings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.viewModel.SettingsViewModel
import com.example.playlistmaker.ui.settings.viewModel.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory())[SettingsViewModel::class.java]

        initObservers()
        initClickListeners()
    }

    private fun initObservers() {
        // переключение темы
        settingsViewModel.isDarkThemeEnabled.observe(this) { isEnabled ->
            settingsBinding.themeSwitcher.isChecked = isEnabled
        }
    }

    private fun initClickListeners() {
        settingsBinding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.switchTheme(isChecked)
        }

        // поделиться приложением
        settingsBinding.shareSettings.setOnClickListener{
            settingsViewModel.shareApp(this)
        }

        // написать в поддержку
        settingsBinding.supportSettings.setOnClickListener{
            settingsViewModel.contactSupport(this)
        }

        // пользовательское соглашение
        settingsBinding.userAgreementSettings.setOnClickListener {
            settingsViewModel.openUserAgreement(this)
        }

        // кнопка "назад"
        settingsBinding.settingsToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}