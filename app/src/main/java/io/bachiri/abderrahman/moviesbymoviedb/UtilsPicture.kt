package io.bachiri.abderrahman.moviesbymoviedb

object UtilsPicture {

    private const val MOVIE_PICTURES_POSTER_BASE_URL_W500 = "https://image.tmdb.org/t/p/w500/"

    fun getCompleteUrlFrom(moviePosterPath: String): String {
        return MOVIE_PICTURES_POSTER_BASE_URL_W500 + moviePosterPath
    }
}