package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistInfoFragmentBinding
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.library.viewModel.PlaylistsViewModel
import com.example.playlistmaker.ui.player.activity.PlayerFragment
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {
    private lateinit var playlistInfoBinding: PlaylistInfoFragmentBinding
    private lateinit var playlist: Playlist
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private val adapter = TrackAdapter()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playlistInfoBinding = PlaylistInfoFragmentBinding.inflate(inflater, container, false)
        return playlistInfoBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable(PLAYLIST) as Playlist

        bindPlaylist(playlistInfoBinding, playlist)

        playlistInfoBinding.tracksInPlaylistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistInfoBinding.tracksInPlaylistRecyclerView.adapter = adapter

        playlistsViewModel.loadTracks(playlist.trackIds)

        playlistsViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (!tracks.isNullOrEmpty()) {
                adapter.tracks.clear()
                adapter.tracks.addAll(tracks)
                adapter.notifyDataSetChanged()
            }
        }

        adapter.onItemClick = { track ->
            startPlayerActivity(track)
        }

        initClickListeners()
        initBottomSheet()
    }

    private fun initClickListeners() {
        playlistInfoBinding.playlistInfoToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(playlistInfoBinding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED

        }

    }

    private fun bindPlaylist(binding: PlaylistInfoFragmentBinding, playlist: Playlist) {
        with(binding) {
            Glide.with(binding.playlistCoverImage)
                .load(playlist.coverUri)
                .placeholder(R.drawable.ic_mock_cover)
                .error(R.drawable.ic_mock_cover)
                .fallback(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(playlistCoverImage)

            playlistNameTextView.text = playlist.name
            playlistDescriptionTextView.text = playlist.description
            trackQuantityTextView.text = requireContext().resources.getQuantityString(R.plurals.tracks_count, playlist.trackCount, playlist.trackCount)

            playlistsViewModel.playlistDuration.observe(viewLifecycleOwner) { minutes ->
                minutesTotalTextView.text = requireContext().resources.getQuantityString(R.plurals.minutes_count, minutes.toInt(), minutes.toInt())
            }
            playlistsViewModel.calculatePlaylistDuration(playlist.trackIds)
        }
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_playerFragment, PlayerFragment.createArgs(track))
    }

    companion object {
        private const val PLAYLIST = "PLAYLIST"

        fun createArgs(playlist: Playlist): Bundle {
            return Bundle().apply {
                putSerializable(PLAYLIST, playlist)
            }
        }
    }
}