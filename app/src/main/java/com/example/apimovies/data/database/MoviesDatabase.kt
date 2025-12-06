package com.example.apimovies.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apimovies.data.database.entities.MovieEntity

/**
 * Base de datos principal de la aplicación usando Room.
 *
 * Esta clase abstracta sirve como el punto de acceso a la capa de persistencia SQLite subyacente.
 * Define las entidades (tablas) y los DAOs (objetos de acceso a datos) disponibles.
 */
@Database(
    entities = [MovieEntity::class], // Lista de todas las tablas que conforman la DB
    version = 1,                     // Versión del esquema (se debe incrementar si cambias la estructura)
    exportSchema = false             // Desactiva la exportación del esquema a un archivo JSON (útil para control de versiones, pero opcional)
)
abstract class MoviesDatabase : RoomDatabase() {

    // Define el acceso al DAO. Room se encargará de implementar este método automáticamente.
    abstract fun movieDao(): MovieDao
}