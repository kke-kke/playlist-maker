package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEditText: TextInputEditText
    private lateinit var refreshButton: Button
    private lateinit var errorLayout: LinearLayout
    private lateinit var connectionProblemsLayout: LinearLayout
    private lateinit var networkProblemsLayout: LinearLayout
    private lateinit var trackAdapter: TrackAdapter
    private var trackList = ArrayList<Track>()
    private var lastSearch: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        refreshButton = findViewById(R.id.refreshButton)
        errorLayout = findViewById(R.id.error_layout)
        connectionProblemsLayout = findViewById(R.id.connection_problems_layout)
        networkProblemsLayout = findViewById(R.id.nothing_found_layout)

        // recyclerView
        trackAdapter = TrackAdapter()
        recyclerView = findViewById(R.id.searchResultRecyclerView)
        recyclerView.adapter = trackAdapter

        val backButton = findViewById<ImageButton>(R.id.backArrowSearch)
        inputEditText = findViewById(R.id.searchBar)

        // кнопка "назад"
        backButton.setOnClickListener {
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
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // клавиатура прячется после очистки поля
                if (s.isNullOrEmpty()) {
                    hideKeyboard()
                }
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        trackAdapter.tracks = trackList

        // поиск на клавиатуре
        inputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        // повторение последнего запроса при проблемах с сетью
        refreshButton.setOnClickListener { doRefresh() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_TEXT, DEFAULT_VALUE)
        inputEditText.setText(searchValue)
    }

    private fun hideKeyboard() {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        hideRecycler()
    }

    private fun hideRecycler() {
        recyclerView.visibility = View.GONE
        trackList.clear()
    }

    private fun performSearch(query: String) {
        val searchApi = SearchApi.create()

        if (query.isNotEmpty()) {
            hideAllMessages()

            lastSearch = query
            searchApi.search(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            hideAllMessages()
                        } else {
                            showMessage(networkProblemsLayout)
                        }
                    } else {
                        showMessage(connectionProblemsLayout)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(connectionProblemsLayout)
                }
            })
        }

    }

    private fun showMessage(layout: LinearLayout) {
        hideAllMessages()
        errorLayout.visibility = View.VISIBLE
        when (layout) {
            networkProblemsLayout -> networkProblemsLayout.visibility = View.VISIBLE
            connectionProblemsLayout -> connectionProblemsLayout.visibility = View.VISIBLE
        }
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun hideAllMessages() {
        errorLayout.visibility = View.GONE
        networkProblemsLayout.visibility = View.GONE
        connectionProblemsLayout.visibility = View.GONE
    }

    private fun doRefresh() {
        performSearch(lastSearch)
    }

    companion object {
        private const val SEARCH_TEXT: String = "SEARCH"
        private const val DEFAULT_VALUE = ""
    }

}