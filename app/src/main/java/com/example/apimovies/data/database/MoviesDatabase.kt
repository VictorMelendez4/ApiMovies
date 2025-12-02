package com.example.apimovies.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apimovies.data.database.entities.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}