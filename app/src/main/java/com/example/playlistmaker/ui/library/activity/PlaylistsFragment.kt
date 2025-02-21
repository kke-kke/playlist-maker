package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.ui.library.viewModel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private lateinit var playlistsBinding: PlaylistsFragmentBinding
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private val adapter = PlaylistCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playlistsBinding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return playlistsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsBinding.playlistCardRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsBinding.playlistCardRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, 8, 16))
        playlistsBinding.playlistCardRecyclerView.adapter = adapter

        playlistsViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNullOrEmpty()) {
                showEmpty()
            } else {
                showContent(playlists)
            }
        }

        playlistsBinding.libraryCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment, CreatePlaylistFragment().arguments)
        }

    }

    private fun showEmpty() {
        playlistsBinding.playlistCardRecyclerView.visibility = View.GONE
        playlistsBinding.noPlaylistsLayout.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsBinding.noPlaylistsLayout.visibility = View.GONE
        playlistsBinding.playlistCardRecyclerView.visibility = View.VISIBLE

        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }

}