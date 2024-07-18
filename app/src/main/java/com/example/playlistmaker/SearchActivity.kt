package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var inputEditText: TextInputEditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var trackAdapter: TrackAdapter
    private var trackList = ArrayList<Track>()
    private val networkProblem : String = getString(R.string.connection_problems) + "/n/n" +
            getString(R.string.connection_problems_message)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)

        // recyclerView
        val json: String = assets.open("tracks.json").bufferedReader().use { it.readText() }
        trackAdapter = TrackAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.searchResultRecyclerView)
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

        // поиск на иконку поиска
        inputEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawables = inputEditText.compoundDrawables
                val startDrawable = drawables[0]

                startDrawable?.let {
                    val drawableBounds = it.bounds
                    val drawableWidth = drawableBounds.width()

                    if (event.rawX <= (inputEditText.left + drawableWidth + inputEditText.paddingStart)) {
                        // Запуск логики поиска
                        performSearch(inputEditText.text.toString())
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
        // поиск на клавиатуре
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                true
            }
            false
        }
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
                        showMessage(R.drawable.network_connection_problems, networkProblem)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(R.drawable.network_connection_problems, networkProblem)
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

    companion object {
        private const val SEARCH_TEXT: String = "SEARCH"
        private const val DEFAULT_VALUE = ""
    }

}