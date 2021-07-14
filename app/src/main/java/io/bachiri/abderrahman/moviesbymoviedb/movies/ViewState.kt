package io.bachiri.abderrahman.moviesbymoviedb.movies

class ViewState<T>(
    val loading: Boolean = false,
    val showEmptyData: Boolean = false,
    var resultLoaded: Boolean = true,
    val data: T? = null,
    val errorMessage: String = ""
)