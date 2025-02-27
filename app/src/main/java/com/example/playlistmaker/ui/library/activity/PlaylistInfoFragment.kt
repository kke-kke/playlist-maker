package com.example.playlistmaker.ui.library.activity

import android.app.AlertDialog
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        bindPlaylist(playlist)
        setupRecyclerView()
        initBottomSheet()
        setupObservers()
        initClickListeners()

        playlistsViewModel.loadTracks(playlist.id, playlist.trackIds)

    }

    private fun setupRecyclerView() {
        playlistInfoBinding.tracksInPlaylistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistInfoBinding.tracksInPlaylistRecyclerView.adapter = adapter

        adapter.onItemClick = { track -> startPlayerActivity(track) }
        adapter.onLongItemClick = { track -> showDeleteTrackDialog(track) }
    }

    private fun setupObservers() {
        playlistsViewModel.getTracksLiveData(playlist.id).observe(viewLifecycleOwner) { tracks ->
            submitList(tracks)
            updatePlaylistInfo(tracks)
        }

        playlistsViewModel.playlistDuration.observe(viewLifecycleOwner) { minutes ->
            playlistInfoBinding.minutesTotalTextView.text = requireContext().resources.getQuantityString(
                R.plurals.minutes_count, minutes.toInt(), minutes.toInt()
            )
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setPositiveButton("ДА") { _, _ ->
                playlistsViewModel.removeTrack(track, playlist.id)
            }
            .setNegativeButton("НЕТ", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.main_background_color))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.main_background_color))
            }
    }

    private fun updatePlaylistInfo(tracks: List<Track>) {
        playlistInfoBinding.trackQuantityTextView.text = requireContext().resources.getQuantityString(
            R.plurals.tracks_count, tracks.size, tracks.size
        )

        playlistsViewModel.calculatePlaylistDuration(tracks.map { it.trackId })
    }

    private fun submitList(tracks: List<Track>) {
        if (!tracks.isNullOrEmpty()) {
            adapter.tracks.clear()
            adapter.tracks.addAll(tracks)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initClickListeners() {
        playlistInfoBinding.playlistInfoToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(playlistInfoBinding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        playlistInfoBinding.buttonsLayout.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val buttonsY = playlistInfoBinding.buttonsLayout.y.toInt()
            val peekHeight = screenHeight - buttonsY

            bottomSheetBehavior.peekHeight = peekHeight
        }
    }

    private fun bindPlaylist(playlist: Playlist) {
        with(playlistInfoBinding) {
            Glide.with(playlistInfoBinding.playlistCoverImage)
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