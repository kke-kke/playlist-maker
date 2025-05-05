package com.example.playlistmaker.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.playlistmaker.R

object NavigationHelper {

    fun toSearch() {
        onView(withId(R.id.searchFragment)).perform(click())
    }

    fun toLib() {
        onView(withId(R.id.libraryFragment)).perform(click())
    }

    fun toSettings() {
        onView(withId(R.id.settingsFragment)).perform(click())
    }
}