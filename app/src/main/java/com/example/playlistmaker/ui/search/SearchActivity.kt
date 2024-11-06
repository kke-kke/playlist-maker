package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.Constants.SEARCH_HISTORY
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.SearchHistoryViewModel
import com.example.playlistmaker.presentation.SearchViewModel
import com.example.playlistmaker.presentation.ViewModelFactory
import com.example.playlistmaker.ui.player.PlayerActivity
import java.io.Serializable

class SearchActivity : AppCompatActivity() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var searchBinding: ActivitySearchBinding
    private var trackAdapter = TrackAdapter()
    private var trackHistoryAdapter = TrackSearchHistoryAdapter()
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        searchBinding.searchResultRecyclerView.adapter = trackAdapter
        searchBinding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val sharedPreferences = getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        searchViewModel = ViewModelProvider(this, ViewModelFactory(sharedPreferences))[SearchViewModel::class.java]
        searchHistoryViewModel = ViewModelProvider(this, ViewModelFactory(sharedPreferences))[SearchHistoryViewModel::class.java]

        initObservers()

        // строка поиска
        searchBinding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s.toString()
                if (s.isNullOrEmpty()) {
                    hideRecycler()
                    hideKeyboard()
                } else {
                    searchDebounce(searchValue)
                    searchViewModel.onSearchTextChanged(searchValue)
                }

                if (s.isNullOrEmpty() && trackHistoryAdapter.tracks.isNotEmpty()) {
                    showHistoryLayout()
                } else {
                    hideHistoryLayout()
                }
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    hideKeyboard()
                }
            }
        })

        // кнопка "назад"
        searchBinding.searchToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // повторение последнего запроса при проблемах с сетью
        searchBinding.refreshButton.setOnClickListener { doRefresh() }

        // клик на трек из результатов поиска
        trackAdapter.onItemClick = { track ->
            searchHistoryViewModel.addTrackToHistory(track)
            startPlayerActivity(track)
        }

        // клик на трек в истории поиска
        trackHistoryAdapter.onItemClick = { track ->
            searchHistoryViewModel.addTrackToHistory(track)
            startPlayerActivity(track)
        }

        // кнопка "очистить историю"
        searchBinding.clearHistoryButton.setOnClickListener {
            searchHistoryViewModel.clearHistory()
            hideHistoryLayout()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_TEXT, DEFAULT_VALUE)
        searchBinding.searchBar.setText(searchValue)
    }

    private fun initObservers() {
        searchViewModel.trackList.observe(this) { tracks ->
            when (tracks) {
                is Resource.Success<*> -> {
                    val data = tracks.data as? List<Track>
                    if (data.isNullOrEmpty()) {
                        showMessage(searchBinding.nothingFoundLayout)
                    } else {
                        hideAllMessages()
                        trackAdapter.tracks.clear()
                        trackAdapter.tracks.addAll(data)
                        trackAdapter.notifyDataSetChanged()
                        searchBinding.searchResultRecyclerView.show()
                    }
                }
                is Resource.Error<*> -> {
                    showMessage(searchBinding.connectionProblemsLayout)
                }
            }
            searchBinding.searchProgressBar.gone()
        }

        searchViewModel.isLoading.observe(this) { isLoading ->
            searchBinding.searchProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        searchViewModel.emptyState.observe(this) { isEmpty ->
            if (isEmpty) {
                hideAllMessages()
                showMessage(searchBinding.nothingFoundLayout)
            }
        }

        searchViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                hideAllMessages()
                showMessage(searchBinding.connectionProblemsLayout)
            }
        }

        searchViewModel.searchResults.observe(this) { tracks ->
            trackAdapter.tracks = tracks as ArrayList<Track>
            trackAdapter.notifyDataSetChanged()
            hideLoading()
        }

        searchHistoryViewModel.trackHistoryList.observe(this) { history ->
            trackHistoryAdapter.tracks = ArrayList(history)
            trackHistoryAdapter.notifyDataSetChanged()
            hideLoading()

            if (history.isNotEmpty()) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
        }

    }

    private fun hideKeyboard() {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(searchBinding.searchBar.windowToken, 0)
        hideRecycler()
    }

    private fun hideRecycler() {
        searchBinding.searchResultRecyclerView.gone()
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun searchDebounce(stringToSearch: String) {
        val searchRunnable = Runnable { searchViewModel.performSearch(stringToSearch) }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showMessage(layout: LinearLayout) {
        hideAllMessages()
        searchBinding.errorLayout.show()
        when (layout) {
            searchBinding.nothingFoundLayout -> searchBinding.nothingFoundLayout.show()
            searchBinding.connectionProblemsLayout -> searchBinding.connectionProblemsLayout.show()
        }
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun hideAllMessages() {
        searchBinding.errorLayout.gone()
        searchBinding.nothingFoundLayout.gone()
        searchBinding.connectionProblemsLayout.gone()
    }

    private fun showHistoryLayout() {
        searchBinding.searchHistoryLayout.show()
        searchBinding.hintTextView.show()
        searchBinding.clearHistoryButton.show()
    }

    private fun hideHistoryLayout() {
        searchBinding.searchHistoryLayout.gone()
        searchBinding.hintTextView.gone()
        searchBinding.clearHistoryButton.gone()
    }

    private fun doRefresh() {
        searchViewModel.performSearch(searchValue)
    }

    private fun hideLoading() {
        searchBinding.searchProgressBar.gone()
    }

    private fun startPlayerActivity(track: Track) {
        val playerIntent = Intent(this, PlayerActivity::class.java)
        playerIntent.putExtra("TRACK", track as Serializable)
        startActivity(playerIntent)
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun View.gone() {
        visibility = View.GONE
    }

    companion object {
        private const val SEARCH_TEXT: String = "SEARCH"
        private const val DEFAULT_VALUE = ""
        private const val SEARCH_DEBOUNCE_DELAY  = 2000L
    }

}