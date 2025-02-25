package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.util.Log
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
import com.example.playlistmaker.ui.library.viewModel.PlaylistsViewModel
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

            Log.d("PlaylistInfoFragment", " In bind")
            playlistsViewModel.playlistDuration.observe(viewLifecycleOwner) { minutes ->
                Log.d("PlaylistInfoFragment", " In observe")
                minutesTotalTextView.text = requireContext().resources.getQuantityString(R.plurals.minutes_count, minutes.toInt(), minutes.toInt())
            }
            playlistsViewModel.calculatePlaylistDuration(playlist.trackIds)
        }
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