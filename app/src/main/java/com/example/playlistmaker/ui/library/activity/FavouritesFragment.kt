package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavouritesFragmentBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.library.viewModel.FavouritesViewModel
import com.example.playlistmaker.ui.player.activity.PlayerFragment
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {
    private lateinit var favouritesBinding: FavouritesFragmentBinding
    private val favouritesViewModel by viewModel<FavouritesViewModel>()
    private var adapter = TrackAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        favouritesBinding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return favouritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesBinding.favouritesList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favouritesBinding.favouritesList.adapter = adapter

        favouritesViewModel.favouriteTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                showEmpty()
            } else {
                showContent(tracks)
            }
        }

        adapter.onItemClick = { track ->
            startPlayerActivity(track)
        }
    }

    private fun showEmpty() {
        favouritesBinding.favouritesList.visibility = View.GONE
        favouritesBinding.nothingInFavouritesLayout.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>) {
        favouritesBinding.favouritesList.visibility = View.VISIBLE
        favouritesBinding.nothingInFavouritesLayout.visibility = View.GONE

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(R.id.action_libraryFragment_to_playerFragment, PlayerFragment.createArgs(track))
    }

}