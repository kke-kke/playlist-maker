package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.domain.library.models.Playlist
import com.example.playlistmaker.domain.search.models.Track
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

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl,
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country
        )
    }

    fun map(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            playlistTrackEntity.trackId,
            playlistTrackEntity.trackTitle,
            playlistTrackEntity.artist,
            playlistTrackEntity.trackTimeMillis,
            playlistTrackEntity.artworkUrl100,
            playlistTrackEntity.previewUrl,
            playlistTrackEntity.collectionName,
            playlistTrackEntity.releaseDate,
            playlistTrackEntity.primaryGenreName,
            playlistTrackEntity.country
        )
    }
}