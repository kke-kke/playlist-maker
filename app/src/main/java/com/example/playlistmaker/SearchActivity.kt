package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderLongerMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var connectionProblemsLayout: LinearLayout
    private lateinit var trackAdapter: TrackAdapter
    private var trackList = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderLongerMessage = findViewById(R.id.placeholderLongerMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)
        connectionProblemsLayout = findViewById(R.id.connection_problems_layout)

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
        inputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
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
                        }
                        if (trackList.isEmpty()) {
                            showMessage(R.drawable.nothing_found, getString(R.string.nothing_found))
                        }
                    } else {
                        showNetworkProblemsMessage(R.drawable.network_connection_problems, getString(R.string.connection_problems))
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
//                    trackList.clear()
                    showNetworkProblemsMessage(R.drawable.network_connection_problems, getString(R.string.connection_problems))
                }
            })
        }

    }

    private fun showMessage(image: Int, textMessage: String) {
        if (textMessage.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = textMessage
            if (image != 0) {
                placeholderImage.setImageResource(image)
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
        }
    }

    private fun showNetworkProblemsMessage(image: Int, textMessage: String) {
        if (textMessage.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            connectionProblemsLayout.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = textMessage
            if (image != 0) {
                placeholderImage.setImageResource(image)
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            connectionProblemsLayout.visibility = View.GONE
        }
    }

    private fun doRefresh() {

    }

    companion object {
        private const val SEARCH_TEXT: String = "SEARCH"
        private const val DEFAULT_VALUE = ""
    }

}