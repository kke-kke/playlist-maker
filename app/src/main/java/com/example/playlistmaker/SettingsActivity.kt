package com.example.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        settingsBinding.themeSwitcher.isChecked = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> app.darkTheme
        }

        settingsBinding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            app.switchTheme(isChecked)
            recreate()
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