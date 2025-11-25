package com.example.apimovies.model

import com.google.gson.annotations.SerializedName

data class MovieModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("primaryTitle")
    val title: String,

    @SerializedName("originalTitle")
    val originalTitle: String,

    @SerializedName("startYear")
    val year: Int?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("primaryImage")
    val primaryImage: String?,

    @SerializedName("averageRating")
    val rating: Double?
)
