package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.ui.library.viewModel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {
    private lateinit var playlist: Playlist
    private val editPlaylistViewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getSerializable(PLAYLIST) as Playlist

        editPlaylistViewModel.loadPlaylistData(playlist)

        createPlaylistBinding.createPlaylistToolbar.title = getString(R.string.edit)
        createPlaylistBinding.createPlaylistButton.text = getString(R.string.save_button)

        initEditClickListeners()
        bindPlaylist()
    }

    private fun initEditClickListeners() {
        createPlaylistBinding.createPlaylistToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        createPlaylistBinding.playlistCoverImageContainer.setOnClickListener {
            requestStoragePermission()
        }

        createPlaylistBinding.createPlaylistButton.setOnClickListener {
            val name = createPlaylistBinding.playlistName.text.toString()
            val description = createPlaylistBinding.playlistDescription.text.toString() ?: ""
            val cover = savedCoverUri?.toString() ?: playlist.coverUri ?: ""

            editPlaylistViewModel.updatePlaylist(name, description, cover)
            val result = Bundle().apply {
                putSerializable("UPDATED_PLAYLIST", Playlist(playlist.id, name, description, cover, playlist.trackIds, playlist.trackCount))
            }
            setFragmentResult("EDIT_PLAYLIST_RESULT", result)
            findNavController().navigateUp()

        }
    }

    private fun bindPlaylist() {
        createPlaylistBinding.playlistName.setText(playlist.name)

        if (playlist.description.isNotEmpty()) {
            createPlaylistBinding.playlistDescription.setText(playlist.description)
        } else {
            createPlaylistBinding.playlistDescription.setText("")
        }

        createPlaylistBinding.playlistCoverImageContainer.background = null

        createPlaylistBinding.playlistCoverImage.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

         Glide.with(createPlaylistBinding.playlistCoverImage)
             .load(playlist.coverUri)
             .placeholder(R.drawable.ic_mock_cover)
             .error(R.drawable.ic_mock_cover)
             .fallback(R.drawable.ic_mock_cover)
             .fitCenter()
             .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
             .into(createPlaylistBinding.playlistCoverImage)

        createPlaylistBinding.playlistCoverImage.tag = playlist.coverUri
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