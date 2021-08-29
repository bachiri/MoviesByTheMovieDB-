package io.bachiri.abderrahman.moviesbymoviedb.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.repository.MovieRepository
import io.bachiri.abderrahman.moviesbymoviedb.repository.Resource
import io.bachiri.abderrahman.moviesbymoviedb.utils.UtilsPicture
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val moviesRepository: MovieRepository) :
    ViewModel() {

    private val _movies: MutableStateFlow<MoviesViewState<Movie>> =
        MutableStateFlow(MoviesViewState())
    val movies = _movies

    private val _currentMovie: MutableStateFlow<MovieViewState> = MutableStateFlow(MovieViewState())
    val currentMovie = _currentMovie

    private val _moviesFirstPictures: MutableStateFlow<MoviesViewState<String>> =
        MutableStateFlow(MoviesViewState())
    val moviesFirstPictures = _moviesFirstPictures

    val intents: Channel<MoviesIntent> = Channel(Channel.UNLIMITED)

    private val _effects: Channel<MoviesViewEffect> = Channel()
    val effects = _effects.receiveAsFlow()

    private val localMovies: MutableList<Movie> = mutableListOf()
    private var page: Int = 1
    private var currentSortingType: Sorting = Sorting.NORMAL

    init {
        handleIntents()
        getTopRatedTvShows()
    }

    private fun handleIntents() {

        viewModelScope.launch {
            intents.consumeAsFlow().collect { moviesIntent ->
                when (moviesIntent) {
                    MoviesIntent.MoviesLoadMore -> {
                        loadMore()
                    }
                    MoviesIntent.MoviesRefresh -> {
                        refreshData()
                    }
                    is MoviesIntent.MoviesSortingIntent -> {
                        setSortingType(moviesIntent.sortingType)
                    }

                    is MoviesIntent.MoviesOnMovieClicked -> {
                        setCurrentSelectedMovie(moviesIntent.id)
                        setEffect(MoviesViewEffect.MoviesOpenMovieView)
                    }
                }
            }
        }
    }

    private fun setEffect(moviesOpenMovieView: MoviesViewEffect) {
        viewModelScope.launch {
            _effects.send(moviesOpenMovieView)
        }
    }

    private fun setCurrentSelectedMovie(id: Int) {
        viewModelScope.launch {
            _currentMovie.emit(MovieViewState(data = localMovies.firstOrNull { it.id == id }))
        }
    }

    private fun refreshData() {
        page = 1
        localMovies.clear()
        getTopRatedTvShows()
    }

    private fun loadMore() {
        page++
        getTopRatedTvShows()
    }

    private fun setSortingType(sortingType: Sorting) {
        if (sortingType != currentSortingType) {
            currentSortingType = sortingType
            emitSortedData()
        }

    }

    private fun emitSortedData() {
        viewModelScope.launch {
            _movies.emit(
                MoviesViewState(
                    loading = false,
                    sortingApplied = true,
                    data = getSortedList(localMovies.toList()),
                    showEmptyData = localMovies.isEmpty()
                )
            )
        }
    }

    private fun getSortedList(toList: List<Movie>): List<Movie> {
        var listToReturn = toList
        when (currentSortingType) {
            Sorting.NORMAL -> {
            }
            Sorting.ALPHABETICAl -> {
                listToReturn = listToReturn.sortedBy { it.movieName }
            }
            Sorting.CHRONOLOGICAL -> {
                listToReturn = listToReturn.sortedByDescending { it.firstAirDate }
            }
        }
        return listToReturn
    }



    private fun getTopRatedTvShows() {

        viewModelScope.launch {
            _movies.emit(
                MoviesViewState(
                    loading = true
                )
            )
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
                            MoviesViewState(
                                loading = false,
                                data = getSortedList(localMovies.toList()),
                                showEmptyData = result.data.isEmpty(),

                            )

                        }
                        is Resource.Loading -> MoviesViewState(
                            loading = true,

                        )
                        is Resource.Error -> MoviesViewState(
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
        _moviesFirstPictures.emit(MoviesViewState(data = firstFivePicture))
    }

    enum class Sorting {
        NORMAL, ALPHABETICAl, CHRONOLOGICAL
    }

    sealed class MoviesIntent {
        class MoviesSortingIntent(val sortingType: Sorting) : MoviesIntent()
        class MoviesOnMovieClicked(val id: Int) : MoviesIntent()
        object MoviesRefresh : MoviesIntent()
        object MoviesLoadMore : MoviesIntent()

    }

     data class MoviesViewState<T>(
        val loading: Boolean = false,
        val sortingApplied: Boolean = false,
        val showEmptyData: Boolean = false,
        var resultLoaded: Boolean = true,
        val data: List<T> = listOf(),
        val errorMessage: String = ""
    ) : ViewState<List<T>>()


    data class MovieViewState(
        val data: Movie? = null,
    ) : ViewState<Movie>()


    sealed class MoviesViewEffect {
        object MoviesOpenMovieView : MoviesViewEffect()
    }
}


