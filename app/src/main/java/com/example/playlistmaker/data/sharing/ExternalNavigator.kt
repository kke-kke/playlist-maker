package com.example.playlistmaker.data.sharing

import android.content.Context
import com.example.playlistmaker.domain.sharing.models.EmailData

interface ExternalNavigator {
    fun shareLink(context: Context, link: String)
    fun openLink(context: Context, link: String)
    fun openEmail(context: Context, email: EmailData)
}