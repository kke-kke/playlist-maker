package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackId")
    val trackId: Int,
    @SerializedName("trackName")
    val trackName: String, // Название композиции
    @SerializedName("artistName")
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis")
    val trackTime: Long, // Продолжительность трека
    @SerializedName("artworkUrl100")
    val artworkUrl100: String, // Ссылка на изображение обложки
    @SerializedName("collectionName")
    val collectionName: String, // Название альбома
    @SerializedName("releaseDate")
    val releaseDate: String, // год релиза трека
    @SerializedName("primaryGenreName")
    val primaryGenreName: String, // жанр трека
    @SerializedName("country")
    val country: String // страна исполнителя
) : Serializable {

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

}