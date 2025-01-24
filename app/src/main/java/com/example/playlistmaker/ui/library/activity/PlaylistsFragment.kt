package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding

class PlaylistsFragment : Fragment() {
    private lateinit var playlistsBinding: PlaylistsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playlistsBinding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return playlistsBinding.root
    }

}