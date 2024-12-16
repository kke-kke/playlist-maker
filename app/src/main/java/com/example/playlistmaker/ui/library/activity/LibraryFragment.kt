package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.LibraryFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {
    companion object {
        private const val TRACK_ID = "track_id"
        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(trackId: String, playlistId: String) = LibraryFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_ID, trackId)
                putString(PLAYLIST_ID, playlistId)
            }
        }
    }

    private lateinit var libraryBinding: LibraryFragmentBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        libraryBinding = LibraryFragmentBinding.inflate(inflater, container, false)
        return libraryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackId = arguments?.getString(TRACK_ID) ?: ""
        val playlistId = arguments?.getString(PLAYLIST_ID) ?: ""

        libraryBinding.libraryViewPager.adapter = LibraryPagerAdapter(childFragmentManager, lifecycle, trackId, playlistId)

        tabMediator = TabLayoutMediator(libraryBinding.libraryTabLayout, libraryBinding.libraryViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        libraryBinding.libraryToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}