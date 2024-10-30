package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.Constants.SETTINGS_PREFERENCES
import com.example.playlistmaker.Constants.THEME_KEY
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        // кнопка "назад"
        settingsBinding.settingsToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // переключение темы
        val app = applicationContext as App
        val sharedPreferences = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE)
        settingsBinding.themeSwitcher.isChecked = sharedPreferences.getBoolean(THEME_KEY, false)

        settingsBinding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            app.switchTheme(isChecked)
        }

        // поделиться приложением
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        settingsBinding.shareSettings.setOnClickListener{
            startActivity(shareIntent)
        }

        // написать в поддержку
        val supportIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_default_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
        }

        settingsBinding.supportSettings.setOnClickListener{
            startActivity(supportIntent)
        }

        // пользовательское соглашение
        val webpage: Uri = Uri.parse(getString(R.string.user_agreement_link))
        val intent = Intent(Intent.ACTION_VIEW, webpage)

        settingsBinding.userAgreementSettings.setOnClickListener {
            startActivity(intent)
        }

    }

}