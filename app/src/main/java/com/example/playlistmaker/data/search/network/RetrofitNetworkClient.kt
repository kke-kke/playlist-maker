package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val searchApi: SearchApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            withContext(Dispatchers.IO) {
                val response = searchApi.search(dto.expression)
                response.apply { resultCode = 200 }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}