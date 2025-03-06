package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.data.db.dao.TrackDao

@Database(version = 6, entities = [
    TrackEntity::class,
    PlaylistEntity::class,
    PlaylistTrackEntity::class
])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getPlaylistTrackDao(): PlaylistTrackDao
}
