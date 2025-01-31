package com.example.playlistmaker.domain.search.models

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: Long, // Продолжительность трека
    val artworkUrl: String, // Ссылка на изображение обложки
    val previewUrl: String?, // ссылка на отрывок трека
    val collectionName: String, // Название альбома
    val releaseDate: String, // год релиза трека
    val primaryGenreName: String, // жанр трека
    val country: String, // страна исполнителя
    var isFavourite: Boolean = false
) : Serializable {

    fun getCoverArtwork() = artworkUrl.replaceAfterLast('/',"512x512bb.jpg")

}