package io.bachiri.abderrahman.moviesbymoviedb.repository

import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.data.remote.MovieDto
import javax.inject.Inject

interface MovieMapper {
    fun mapToMovie(movieDto: MovieDto): Movie
}

class MovieMapperImpl @Inject constructor(): MovieMapper {

    override fun mapToMovie(movieDto: MovieDto): Movie {
        return Movie(
            movieDto.id,
            movieDto.moviePosterPath,
            movieDto.movieName,
            movieDto.overview,
            movieDto.voteCount,
            movieDto.voteAverage
        )
    }

}