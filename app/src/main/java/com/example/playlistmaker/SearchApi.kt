package com.example.playlistmaker

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    companion object {
        fun create(): SearchApi {
            return Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SearchApi::class.java)
        }

    }

    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>

}

data class TrackResponse(
    val results: List<Track>
)
