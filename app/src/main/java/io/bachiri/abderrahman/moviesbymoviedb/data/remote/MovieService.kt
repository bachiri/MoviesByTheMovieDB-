package io.bachiri.abderrahman.moviesbymoviedb.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    companion object {
        const val TV_BASE_URL = "tv"
        const val TV_TOP_RATED_BASE_URL = "/top_rated"
    }

    @GET(TV_BASE_URL + TV_TOP_RATED_BASE_URL)
    suspend fun getTopRatedTVShows(
        @Query("page") page: Int,
    ): MovieDtoResponse
}