package com.example.playlistmaker.ui.library.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
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
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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

        setFragmentResultListener("EDIT_PLAYLIST_RESULT") { _, bundle ->
            val updatedPlaylist = bundle.getSerializable("UPDATED_PLAYLIST") as? Playlist
            updatedPlaylist?.let {
                playlist = it
                bindPlaylist(it)
                playlistsViewModel.loadTracks(it.id, it.trackIds)
            }
        }
    }

    private fun setupRecyclerView() {
        playlistInfoBinding.tracksInPlaylistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistInfoBinding.tracksInPlaylistRecyclerView.adapter = adapter

        adapter.onItemClick = { track -> startPlayerActivity(track) }
        adapter.onLongItemClick = { track -> showDeleteTrackDialog(track) }
    }

    private fun setupObservers() {
        playlistsViewModel.playlist.observe(viewLifecycleOwner) { updatedPlaylist ->
            if (updatedPlaylist != null) {
                bindPlaylist(updatedPlaylist)
            }
        }

        playlistsViewModel.getTracksLiveData(playlist.id).observe(viewLifecycleOwner) { tracks ->
            submitList(tracks)
            updatePlaylistInfo(tracks)
        }

        playlistsViewModel.playlistDuration.observe(viewLifecycleOwner) { minutes ->
            playlistInfoBinding.minutesTotalTextView.text = requireContext().resources.getQuantityString(
                R.plurals.minutes_count, minutes.toInt(), minutes.toInt()
            )
        }

        playlistsViewModel.deletionState.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                findNavController().navigateUp()
            }
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

        playlistInfoBinding.shareButtonPlaylist.setOnClickListener {
            sharePlaylist()
        }

        playlistInfoBinding.menuButtonPlaylist.setOnClickListener {
            bindMenuPlaylist(playlist)
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        playlistInfoBinding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistInfoBinding.menuShare.setOnClickListener{
            sharePlaylist()
        }

        playlistInfoBinding.menuEditInfo.setOnClickListener{
            startEditPlaylistActivity()
        }

        playlistInfoBinding.menuDetelePlaylist.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(playlistInfoBinding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED

            playlistInfoBinding.buttonsLayout.post {
                val screenHeight = resources.displayMetrics.heightPixels
                val buttonsBottom = playlistInfoBinding.buttonsLayout.bottom + 24
                val peekHeight = screenHeight - buttonsBottom

                bottomSheetBehavior.peekHeight = peekHeight
            }
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(playlistInfoBinding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_EXPANDED -> {
                            playlistInfoBinding.overlay.visibility = View.GONE
                        }
                        else -> {
                            playlistInfoBinding.overlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить плейлист \"${playlist.name}\"?")
            .setPositiveButton("ДА") { _, _ ->
                playlistsViewModel.deletePlaylist(playlist.id)
            }
            .setNegativeButton("НЕТ", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.main_background_color))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.main_background_color))
            }
    }

    private fun sharePlaylist() {
        if (playlist.trackIds.isNotEmpty()) {
            playlistsViewModel.sharePlaylist(requireContext(), playlist)
        } else {
            Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_LONG).show()
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

    private fun bindMenuPlaylist(playlist: Playlist){
        with(playlistInfoBinding) {
            Glide.with(playlistInfoBinding.menuPlaylistCoverImage)
                .load(playlist.coverUri)
                .placeholder(R.drawable.ic_mock_cover)
                .error(R.drawable.ic_mock_cover)
                .fallback(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(menuPlaylistCoverImage)

            menuPlaylistNameTextView.text = playlist.name
            menuSongQuantity.text = requireContext().resources.getQuantityString(R.plurals.tracks_count, playlist.trackCount, playlist.trackCount)
        }
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_playerFragment, PlayerFragment.createArgs(track))
    }

    private fun startEditPlaylistActivity() {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_editPlaylistFragment, EditPlaylistFragment.createArgs(playlist))
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