package io.bachiri.abderrahman.moviesbymoviedb.data.remote

import com.google.gson.annotations.SerializedName

class MovieDtoResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

class MovieDto(
    val id: Int,
    @SerializedName("poster_path") val moviePosterPath: String?,
    @SerializedName("name") val movieName: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("first_air_date") val firstAirDate: String
)
