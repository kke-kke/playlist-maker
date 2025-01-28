package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrackEntity(trackEntity: TrackEntity)

    @Query("DELETE FROM track_table WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM track_table ORDER BY dateSaved DESC")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM track_table WHERE track_id = :trackId")
    fun isTrackFavourite(trackId: Int): Flow<Int>

}