package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)

    @Query("UPDATE playlist_table SET playlistName = :name, playlistDescription = :description, coverUri = :coverUri WHERE playlistId = :playlistId")
    suspend fun updatePlaylist(playlistId: Int, name: String, description: String, coverUri: String)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    fun getPlaylistById(id: Int): Flow<PlaylistEntity?>

    @Query("UPDATE playlist_table SET trackIds = :newTrackIds, trackCount = :newTrackCount WHERE playlistId = :playlistId")
    suspend fun updateTrackIdsInPlaylist(playlistId: Int, newTrackIds: String, newTrackCount: Int)

}