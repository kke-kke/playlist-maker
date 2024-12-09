package com.example.playlistmaker.ui.library.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FavouritesFragmentBinding

class FavouritesFragment : Fragment() {
    companion object {
        private const val FAV_TRACK_ID = "fav_track_id"

        fun newInstance(trackId: String) = FavouritesFragment().apply {
            arguments = Bundle().apply {
                putString(FAV_TRACK_ID, trackId)
            }
        }
    }

    private lateinit var favouritesBinding: FavouritesFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        favouritesBinding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return favouritesBinding.root
    }

}