package io.bachiri.abderrahman.moviesbymoviedb.data.remote

import kotlinx.coroutines.delay
import java.io.IOException

//This Network Service is for testing purposes

class FakeNetworkMovieService : MovieService {
    override suspend fun getTopRatedTVShows(
        page: Int
    ): MovieDtoResponse {

        delay(3000)//TO simulate the network delay


        //To trigger an exception follow the instruction in the TODO comment
        val exceptionResponse = getExceptionResponse()
        exceptionResponse.invoke()//TODO uncomment this line to get an exception


        val movieDtoResponse: MovieDtoResponse = getNonEmptyResponse()
        return movieDtoResponse //TODO uncomment this line to get a list of results that is not empty


        //val emptyDtoResponse: MovieDtoResponse = getEmptyResponse()
        //return emptyDtoResponse
    }

    private fun getNonEmptyResponse() = MovieDtoResponse(
        1, listOf<MovieDto>(
            MovieDto(
                1,
                "/A39DWUIrf9WDRHCg7QTR8seWUvi.jpg",
                "Young Royals",
                "Prince Wilhelm adjusts to life at his prestigious new boarding school, Hillerska, but following his heart proves more challenging than anticipated.",
                143,
                9.4F
            ),
            MovieDto(
                2,
                "/qG59J1Q7rpBc1dvku4azbzcqo8h.jpg",
                "I Am Not an Animal",
                "I Am Not An Animal is an animated comedy series about the only six talking animals in the world, whose cosseted existence in a vivisection unit is turned upside down when they are liberated by animal rights activists.",
                653,
                9.5F
            ), MovieDto(
                3,
                "/xxv8Ibs8Anni6qrWkAf60rDsPCu.jpg",
                "Run BTS!",
                "Run BTS! is a South Korean show by the boy band BTS. The show is all about BTS doing activities, challenges and lots more.",
                143,
                9.4F
            ),
            MovieDto(
                4,
                "/6cXf5EDwVhsRv8GlBzUTVnWuk8Z.jpg",
                "The Rising of the Shield Hero",
                "Prince Wilhelm adjusts to life at his prestigious new boarding school, Hillerska, but following his heart proves more challenging than anticipated.",
                553,
                9.1F
            )
        ), 1,
        4
    )

    private fun getEmptyResponse() = MovieDtoResponse(1, listOf(), 1, 0)

    private fun getExceptionResponse(): () -> Unit = {
        throw IOException("Error In your network")
    }
}