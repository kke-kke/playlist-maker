package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun getTrackById(trackId: Int, playlistId: Int): PlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds) ORDER BY dateAdded DESC")
    suspend fun getTracksByIds(trackIds: List<Int>): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deleteTracksByPlaylistId(playlistId: Int)

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
}