package io.bachiri.abderrahman.moviesbymoviedb.data

data class Movie(
     val id:Int,
     val moviePosterPath: String?,
     val movieName: String,
     val overview: String,
     val voteCount: Int,
     val voteAverage: Float
)