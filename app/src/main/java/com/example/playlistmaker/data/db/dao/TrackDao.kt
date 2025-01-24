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

    // метод @Query для получения списка со всеми треками, добавленными в избранное
    @Query("SELECT * FROM track_table")
    fun getTracks(): Flow<List<TrackEntity>>

    // метод @Query для получения списка идентификаторов всех треков, которые добавлены в избранное
    @Query("SELECT track_id FROM track_table")
    suspend fun getTracksId(): List<Int>

    @Query("SELECT COUNT(*) FROM track_table WHERE track_id = :trackId")
    fun isTrackFavourite(trackId: Int): Flow<Int>

}