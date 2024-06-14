package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.backArrowSettings)
        val switchThemes = findViewById<Switch>(R.id.switchThemeSettings)
        val shareButton = findViewById<ImageButton>(R.id.shareSettings)
        val supportButton = findViewById<ImageButton>(R.id.supportSettings)
        val userAgreementButton = findViewById<ImageButton>(R.id.userAgreementSettings)

        // кнопка "назад"
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // переключение темы
        // получаем состояние switch
        val sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val switchState = sharedPreferences.getBoolean(SWITCH_STATE, DEFAULT_VALUE)
        switchThemes.isChecked = switchState
        switchThemes.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(SWITCH_STATE, isChecked)
            editor.apply()
        }

        // поделиться приложением
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        shareButton.setOnClickListener{
            startActivity(shareIntent)
        }

        // написать в поддержку
        val supportIntent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_default_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
        }

        supportButton.setOnClickListener{
            startActivity(supportIntent)
        }

        // пользовательское соглашение
        val webpage: Uri = Uri.parse(getString(R.string.user_agreement_link))
        val intent = Intent(Intent.ACTION_VIEW, webpage)

        userAgreementButton.setOnClickListener {
            startActivity(intent)
        }

    }

    companion object {
        private const val SWITCH_STATE: String = "SWITCH_STATE"
        private const val DEFAULT_VALUE = false
        private const val PREFERENCES = "My_Preferences"
    }
}