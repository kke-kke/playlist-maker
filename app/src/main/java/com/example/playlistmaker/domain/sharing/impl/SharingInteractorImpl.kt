package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.models.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    override fun shareApp(context: Context) {
        externalNavigator.shareLink(context, getShareAppLink(context))
    }

    override fun openTerms(context: Context) {
        externalNavigator.openLink(context, getTermsLink(context))
    }

    override fun openSupport(context: Context) {
        externalNavigator.openEmail(context, getSupportEmailData(context))
    }

    override fun sharePlaylist(context: Context, name: String, description: String, trackCount: String, tracks: List<String>) {
        val tracksText = tracks.joinToString("\n")

        val playlistText = """
            |$name
            |$description
            |$trackCount
            |$tracksText
            """.trimMargin()

        externalNavigator.shareLink(context, playlistText)
    }


    private fun getShareAppLink(context: Context): String {
        return context.getString(R.string.share_link)
    }

    private fun getSupportEmailData(context: Context): EmailData {
        return EmailData(context.getString(R.string.support_default_email), context.getString(R.string.support_email_subject), context.getString(R.string.support_email_body))
    }

    private fun getTermsLink(context: Context): String {
        return context.getString(R.string.user_agreement_link)
    }
}