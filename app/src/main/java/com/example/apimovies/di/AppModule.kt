package com.example.apimovies.di

import android.app.Application
import androidx.room.Room
import com.example.apimovies.data.MovieApi
import com.example.apimovies.data.database.MovieDao
import com.example.apimovies.data.database.MoviesDatabase
import com.example.apimovies.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo de Configuración (Hilt).
 * Aquí le enseñamos a la aplicación cómo crear las herramientas principales
 * (Conexión a Internet y Base de Datos) para usarlas en cualquier parte.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Configura el "cliente" de internet.
     * Se encarga de pegar automáticamente la API Key en cada petición
     * para no tener que escribirla manualmente siempre
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-RapidAPI-Key", Constants.API_KEY)
                    .addHeader("X-RapidAPI-Host", Constants.API_HOST)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    /**
     * Configura Retrofit
     * Define a qué URL nos conectamos y cómo convertir los datos
     * que llegan (JSON) en objetos de Kotlin que la app entienda
     */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Crea la conexión final con la API.
     * Esta función usaremos en el repositorio para pedir películas.
     */
    @Singleton
    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    /**
     * Crea la Base de Datos en el teléfono.
     * Solo se crea una vez y se reutiliza (Singleton).
     */
    @Singleton
    @Provides
    fun provideDatabase(app: Application): MoviesDatabase {
        return Room.databaseBuilder(
            app,
            MoviesDatabase::class.java,
            "movies_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provee el acceso rápido a las operaciones de la base de datos (DAO).
     * Sirve para guardar y borrar favoritos fácilmente.
     */
    @Singleton
    @Provides
    fun provideMovieDao(db: MoviesDatabase): MovieDao {
        return db.movieDao()
    }
}