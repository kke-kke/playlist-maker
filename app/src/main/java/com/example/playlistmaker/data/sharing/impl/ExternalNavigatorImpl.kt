package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.models.EmailData

class ExternalNavigatorImpl : ExternalNavigator {
    override fun shareLink(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    override fun openLink(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    override fun openEmail(context: Context, email: EmailData) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.emailAddress))
            putExtra(Intent.EXTRA_SUBJECT, email.emailSubject)
            putExtra(Intent.EXTRA_TEXT, email.emailBody)
        }
        context.startActivity(intent)
    }
}