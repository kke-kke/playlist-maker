package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.impl.settings.SettingsInteractorImpl
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModelFactory
import com.example.playlistmaker.utils.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        val app = applicationContext as App
        val interactor = SettingsInteractorImpl(app)
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(interactor))[SettingsViewModel::class.java]

        // переключение темы
        settingsViewModel.isDarkThemeEnabled.observe(this) { isEnabled ->
            settingsBinding.themeSwitcher.isChecked = isEnabled
        }

        settingsBinding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.switchTheme(isChecked)
        }

        initClickListeners()
    }

    private fun initClickListeners() {
        // поделиться приложением
        settingsBinding.shareSettings.setOnClickListener{
            shareApp()
        }

        // написать в поддержку
        settingsBinding.supportSettings.setOnClickListener{
            goToSupport()
        }

        // пользовательское соглашение
        settingsBinding.userAgreementSettings.setOnClickListener {
            openUserAgreement()
        }

        // кнопка "назад"
        settingsBinding.settingsToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun goToSupport() {
        val supportIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_default_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
        }
        startActivity(supportIntent)
    }

    private fun openUserAgreement() {
        val webpage: Uri = Uri.parse(getString(R.string.user_agreement_link))
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(intent)
    }

}