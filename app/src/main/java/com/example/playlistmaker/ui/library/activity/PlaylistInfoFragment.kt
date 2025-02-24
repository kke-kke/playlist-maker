package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.PlaylistInfoFragmentBinding
import com.example.playlistmaker.ui.search.activity.TrackAdapter

class PlaylistInfoFragment : Fragment() {
    private lateinit var playlistInfoBinding: PlaylistInfoFragmentBinding
    private val adapter = TrackAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playlistInfoBinding = PlaylistInfoFragmentBinding.inflate(inflater, container, false)
        return playlistInfoBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistInfoBinding.tracksInPlaylistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistInfoBinding.tracksInPlaylistRecyclerView.adapter = adapter
    }
}