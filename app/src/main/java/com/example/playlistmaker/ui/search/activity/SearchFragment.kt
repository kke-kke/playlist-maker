package com.example.playlistmaker.ui.search.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchFragmentBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.player.activity.PlayerFragment
import com.example.playlistmaker.ui.search.viewModel.SearchHistoryViewModel
import com.example.playlistmaker.ui.search.viewModel.SearchScreenState
import com.example.playlistmaker.ui.search.viewModel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var searchValue: String = DEFAULT_VALUE

    private lateinit var searchBinding: SearchFragmentBinding
    private var trackAdapter = TrackAdapter()
    private var trackHistoryAdapter = TrackSearchHistoryAdapter()
    private val searchViewModel: SearchViewModel by viewModel()
    private val searchHistoryViewModel: SearchHistoryViewModel by viewModel()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchBinding = SearchFragmentBinding.inflate(inflater, container, false)
        return searchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBinding.searchResultRecyclerView.adapter = trackAdapter
        searchBinding.searchHistoryRecyclerView.adapter = trackHistoryAdapter

        initObservers()

        // строка поиска
        searchBinding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!searchBinding.searchBar.hasFocus()) return
                searchValue = s.toString()
                if (s.isNullOrEmpty()) {
                    hideKeyboard()
                    hideRecycler()
                    searchViewModel.resetSearchState()
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
            override fun afterTextChanged(s: Editable?) {}
        })

        initOnClickListeners()

    }

    override fun onPause() {
        super.onPause()
        searchViewModel.cancelSearch()
        searchValue = DEFAULT_VALUE
        searchBinding.searchBar.setText(DEFAULT_VALUE)
        hideRecycler()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        searchValue = savedInstanceState?.getString(SEARCH_TEXT, DEFAULT_VALUE) ?: DEFAULT_VALUE
        searchBinding.searchBar.setText(searchValue)
    }

    private fun initObservers() {
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> searchBinding.searchProgressBar.isVisible = true
                is SearchScreenState.Success -> {
                    val data = state.tracks as? List<Track>
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
                is SearchScreenState.Empty -> {
                    hideAllMessages()
                    hideRecycler()
                    if (trackHistoryAdapter.tracks.isNotEmpty()) {
                        showHistoryLayout()
                    } else {
                        hideHistoryLayout()
                    }
                }
                is SearchScreenState.EmptyResults -> {
                    hideAllMessages()
                    hideRecycler()
                    showMessage(searchBinding.nothingFoundLayout)
                }
                is SearchScreenState.Error -> {
                    hideAllMessages()
                    hideLoading()
                    showMessage(searchBinding.connectionProblemsLayout)
                }
            }
        }

        searchHistoryViewModel.trackHistoryList.observe(viewLifecycleOwner) { history ->
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

    private fun initOnClickListeners() {
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

    private fun hideKeyboard() {
        val context = context ?: return
        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val windowToken = searchBinding.searchBar.windowToken ?: return
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        searchBinding.searchBar.clearFocus()
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
        if (trackAdapter.tracks.isNotEmpty()) {
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }
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
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, PlayerFragment.createArgs(track))
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