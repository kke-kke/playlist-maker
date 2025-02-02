package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding

class PlaylistsFragment : Fragment() {
    private lateinit var playlistsBinding: PlaylistsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playlistsBinding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return playlistsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsBinding.libraryCreatePlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment, CreatePlaylistFragment().arguments)
        }

    }

}