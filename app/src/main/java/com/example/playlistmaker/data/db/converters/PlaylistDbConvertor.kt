package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.library.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor {
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.coverUri,
            Gson().fromJson(playlistEntity.trackIds, object : TypeToken<List<Int>>() {}.type),
            playlistEntity.trackCount)
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverUri,
            Gson().toJson(playlist.trackIds),
            playlist.trackCount)
    }
}