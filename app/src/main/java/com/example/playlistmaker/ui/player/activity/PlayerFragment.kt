package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlayerFragmentBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.player.viewModel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private val playerViewModel: PlayerViewModel by viewModel()
    private lateinit var playerBinding: PlayerFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        playerBinding = PlayerFragmentBinding.inflate(inflater, container, false)
        return playerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = arguments?.getSerializable(TRACK) as Track

        playerViewModel.loadTrack(track)
        bindTrack(playerBinding, track)

        playerViewModel.playerState.observe(viewLifecycleOwner) { state ->
            with(playerBinding) {
                playButton.setImageResource(if (state.isPlaying) R.drawable.pause else R.drawable.play)
                playButton.isEnabled = state.isPrepared
                currentLengthTextView.text = state.currentPosition
            }
        }

        initClickListeners()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerViewModel.release()
    }

    private fun bindTrack(binding: PlayerFragmentBinding, track: Track) {
        with(binding) {
            Glide.with(binding.albumCoverImage)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_mock_cover)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                .into(albumCoverImage)

            songNameTextView.text = track.trackName
            songArtistTextView.text = track.artistName

            songLengthValueTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
            songAlbumValueTextView.text = track.collectionName.ifEmpty { "" }
            songYearValueTextView.text = track.releaseDate.take(4)
            songGenreValueTextView.text = track.primaryGenreName
            songCountryValueTextView.text = track.country
        }
    }

    private fun initClickListeners() {
        playerBinding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        playerBinding.playerToolbar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val TRACK = "TRACK"

        fun createArgs(track: Track): Bundle {
            return Bundle().apply {
                putSerializable(TRACK, track)
            }
        }
    }

}