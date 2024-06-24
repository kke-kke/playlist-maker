package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchActivity : AppCompatActivity() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var inputEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // recyclerView
        val json: String = assets.open("tracks.json").bufferedReader().use { it.readText() }
        val trackList = Gson().fromJson<ArrayList<Track>>(json, object : TypeToken<ArrayList<Track>>() {}.type)
        val trackAdapter = TrackAdapter(trackList)
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

    companion object {
        private const val SEARCH_TEXT: String = "SEARCH"
        private const val DEFAULT_VALUE = ""
    }

}