package com.example.playlistmaker.data.db.converters

import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.search.models.Track

class TrackDbConvertor {

    fun map(track: TrackEntity): Track {
        return Track(track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country
        )
    }

    fun map(track: Track, dateSaved: Long): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl,
            track.previewUrl,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            dateSaved
        )
    }
}