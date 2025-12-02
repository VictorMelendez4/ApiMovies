package com.example.apimovies.di

import com.example.apimovies.data.MovieApi
import com.example.apimovies.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import android.app.Application
import androidx.room.Room
import com.example.apimovies.data.database.MovieDao
import com.example.apimovies.data.database.MoviesDatabase


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


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

    // Retrofit configurado
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Singleton
    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    // 4. Proveer la Base de Datos
    @Singleton
    @Provides
    fun provideDatabase(app: Application): MoviesDatabase {
        return Room.databaseBuilder(
            app,
            MoviesDatabase::class.java,
            "movies_db"
        ).fallbackToDestructiveMigration() // Si cambias la estructura, borra todo y empieza de cero (útil en dev)
            .build()
    }

    // 5. Proveer el DAO (para no tener que llamar a db.dao() cada vez)
    @Singleton
    @Provides
    fun provideMovieDao(db: MoviesDatabase): MovieDao {
        return db.movieDao()
    }

}