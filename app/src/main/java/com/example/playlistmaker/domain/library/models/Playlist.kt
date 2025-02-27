package com.example.playlistmaker.domain.library.models

import java.io.Serializable

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    var trackIds: List<Int>,
    val trackCount: Int
) : Serializable