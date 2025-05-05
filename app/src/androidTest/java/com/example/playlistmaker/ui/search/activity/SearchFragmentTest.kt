package com.example.playlistmaker.ui.search.activity

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.main.activity.MainActivity
import com.example.playlistmaker.utils.NavigationHelper
import com.example.playlistmaker.utils.RecyclerViewItemCountIdlingResource
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun search_displaysResults() {
        NavigationHelper.toSearch()

        onView(withId(R.id.searchBar))
            .perform(typeText("Beatles"), closeSoftKeyboard())

        onView(withId(R.id.searchResultRecyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        lateinit var recyclerView: RecyclerView
        activityRule.scenario.onActivity { activity ->
            recyclerView = activity.findViewById(R.id.searchResultRecyclerView)
        }

        val idlingResource = RecyclerViewItemCountIdlingResource(recyclerView, minCount = 1)
        Espresso.registerIdlingResources(idlingResource)

        onView(withId(R.id.searchResultRecyclerView))
            .check(matches(hasMinimumChildCount(1)))
        Espresso.unregisterIdlingResources(idlingResource)

    }

}
