package com.example.playlistmaker.domain.library.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val trackIds: List<Int>,
    val trackCount: Int
)