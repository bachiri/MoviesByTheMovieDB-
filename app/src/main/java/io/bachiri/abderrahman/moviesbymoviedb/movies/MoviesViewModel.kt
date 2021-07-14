package io.bachiri.abderrahman.moviesbymoviedb.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bachiri.abderrahman.moviesbymoviedb.UtilsPicture
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieRepository
import io.bachiri.abderrahman.moviesbymoviedb.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val moviesRepository: MovieRepository) :
    ViewModel() {

    private val _movies: MutableStateFlow<ViewState<List<Movie>>> = MutableStateFlow(ViewState())
    val movies = _movies

    private val _moviesFirstPictures: MutableStateFlow<ViewState<List<String>>> =
        MutableStateFlow(ViewState())
    val moviesFirstPictures = _moviesFirstPictures
    private val localMovies: MutableList<Movie> = mutableListOf()
    private var page: Int = 1

    init {
        getTopRatedTvShows()
    }

    fun refreshData() {
        page = 1
        localMovies.clear()
        getTopRatedTvShows()
    }

    fun loadMore() {
        page++
        getTopRatedTvShows()
    }

    private fun getTopRatedTvShows() {

        viewModelScope.launch {
            _movies.emit(ViewState(loading = true))

            moviesRepository
                .getTopRatedTvShows(page)
                .collect { result ->
                    val viewState = when (result) {
                        is Resource.Success -> {
                            localMovies.addAll(result.data)
                            //Set Random pictures for the top Trending CTA
                            if (!result.data.isNullOrEmpty()) {
                                managePicturesPostersForCTA(result)
                            }
                            ViewState(
                                loading = false,
                                data = localMovies.toList(),
                                showEmptyData = result.data.isEmpty()
                            )

                        }
                        is Resource.Loading -> ViewState(loading = true)
                        is Resource.Error -> ViewState(
                            loading = false,
                            errorMessage = result.message
                        )
                    }

                    _movies.emit(viewState)
                }
        }

    }

    private suspend fun managePicturesPostersForCTA(result: Resource.Success<List<Movie>>) {
        val firstFivePicture = mutableListOf<String>()
        for (i in 0..5) {
            val randomNumber = (result.data.indices).random()
            result.data[randomNumber].moviePosterPath?.let { currentPicturePoster ->
                firstFivePicture.add(
                    UtilsPicture.getCompleteUrlFrom(
                        currentPicturePoster
                    )
                )
            }
        }
        _moviesFirstPictures.emit(ViewState(data = firstFivePicture))
    }


}


