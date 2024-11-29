package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest

class RetrofitNetworkClient(private val searchApi: SearchApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val response = searchApi.search(dto.expression).execute()

            val networkResponse = response.body() ?: Response()

            return networkResponse.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}