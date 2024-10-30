package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Constants.SEARCH_HISTORY
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class SearchActivity : AppCompatActivity() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var searchBinding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private var trackList = ArrayList<Track>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var trackHistoryAdapter: TrackSearchHistoryAdapter
    private lateinit var historyTrackList: ArrayList<Track>
    private lateinit var searchHistory: SearchHistory
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        // recyclerView с результатами поиска
        trackAdapter = TrackAdapter()
        searchBinding.searchResultRecyclerView.adapter = trackAdapter

        // recyclerView с историей поиска
        trackHistoryAdapter = TrackSearchHistoryAdapter()
        searchBinding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        searchBinding.searchHistoryRecyclerView.layoutManager = layoutManager

        // кнопка "назад"
        searchBinding.searchToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // строка поиска
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s.toString()
                if (s.isNullOrEmpty()) {
                    hideRecycler()
                } else {
                    searchBinding.searchResultRecyclerView.show()
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // клавиатура прячется после очистки поля
                if (s.isNullOrEmpty()) {
                    hideKeyboard()
                }
            }
        }
        searchBinding.searchBar.addTextChangedListener(simpleTextWatcher)

        trackAdapter.tracks = trackList

        // повторение последнего запроса при проблемах с сетью
        searchBinding.refreshButton.setOnClickListener { doRefresh() }

        sharedPreferences = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)

        // инициализация класса истории поиска
        searchHistory = SearchHistory(sharedPreferences)

        historyTrackList = searchHistory.loadTrackHistory()
        trackHistoryAdapter.tracks = historyTrackList

        searchBinding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() && trackHistoryAdapter.tracks.isNotEmpty()) {
                    showHistoryLayout()
                } else {
                    hideHistoryLayout()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        })

        // добавление трека в историю из результатов поиска
        trackAdapter.onItemClick = { track ->
            searchHistory.addTrackToHistory(track)

            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("TRACK", track as Serializable)
            startActivity(playerIntent)

            trackHistoryAdapter.tracks = searchHistory.loadTrackHistory()
            trackHistoryAdapter.notifyDataSetChanged()

        }

        // клик на трек в истории поиска
        trackHistoryAdapter.onItemClick = { track ->
            searchHistory.addTrackToHistory(track)

            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("TRACK", track as Serializable)
            startActivity(playerIntent)

            trackHistoryAdapter.tracks = searchHistory.loadTrackHistory()
            trackHistoryAdapter.notifyDataSetChanged()
        }

        // отслеживание фокуса на поисковую строку
        searchBinding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchBinding.searchBar.text?.isEmpty() == true && trackHistoryAdapter.tracks.isNotEmpty()) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
        }

        // кнопка "очистить историю"
        searchBinding.clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()

            trackHistoryAdapter.tracks.clear()
            trackHistoryAdapter.notifyDataSetChanged()

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

    private fun hideKeyboard() {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(searchBinding.searchBar.windowToken, 0)
        hideRecycler()
    }

    private fun hideRecycler() {
        searchBinding.searchResultRecyclerView.gone()
        trackList.clear()
    }

    private fun performSearch() {
        val searchApi = SearchApi.create()

        if (searchValue.isNotEmpty()) {
            hideAllMessages()
            searchBinding.searchProgressBar.show()

            searchApi.search(searchValue).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    searchBinding.searchProgressBar.gone()
                    if (response.code() == 200) {
                        trackList.clear()
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            trackList.addAll(results)
                            trackAdapter.notifyDataSetChanged()
                            hideAllMessages()
                        } else {
                            showMessage(searchBinding.nothingFoundLayout)
                        }
                    } else {
                        showMessage(searchBinding.connectionProblemsLayout)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    searchBinding.searchProgressBar.gone()
                    showMessage(searchBinding.connectionProblemsLayout)
                }
            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showMessage(layout: LinearLayout) {
        hideAllMessages()
        searchBinding.errorLayout.show()
        when (layout) {
            searchBinding.nothingFoundLayout -> searchBinding.nothingFoundLayout.show()
            searchBinding.connectionProblemsLayout -> searchBinding.connectionProblemsLayout.show()
        }
        trackList.clear()
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
        performSearch()
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