# MoviesByTheMovieDB

## Screens(With Some technical description) 

First when the app is opened a top rating CTA is displayed that contains 5 pictures the fragment is associated with a viewModel that loads data and it's the same view model shared with the listing view and depending on the movies posters the pictures are [selected randomly](https://github.com/bachiri/MoviesByTheMovieDB-/blob/0a2146852807c96b7390adb7fd883f116043476e/app/src/main/java/io/bachiri/abderrahman/moviesbymoviedb/movies/MoviesViewModel.kt#L78)

When those pictures are updated ?

  * During the first load 
  
  * During a pagination request they are also randomly picked from all the movies
  
  * During a refresh

<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/HomeScreenEmptyPosters.png" />


<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/HomeScreenWithPosters.png" />


Listing Screen that displays the top rated tv shows/movies and has the ability to sort by movie name or by date of publication

 * Sorting is also applied when fetching new results after the pagination [using this mehod](https://github.com/bachiri/MoviesByTheMovieDB-/blob/e00bf32421c472cd8dde56c4c414f88345e9f0d8/app/src/main/java/io/bachiri/abderrahman/moviesbymoviedb/movies/MoviesViewModel.kt#L64) .

<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/MoviesScreen.png" />

How to see an empty Trending movies 
  * There is a class [FakeNetworkMovieService](https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/app/src/main/java/io/bachiri/abderrahman/moviesbymoviedb/data/remote/FakeNetworkMovieService.kt) that can be used by returning an empty list
  ```
    val emptyDtoResponse: MovieDtoResponse = getEmptyResponse()
      return emptyDtoResponse
  ```
  and also injecting the [fake service](https://github.com/bachiri/MoviesByTheMovieDB-/blob/0a2146852807c96b7390adb7fd883f116043476e/app/src/main/java/io/bachiri/abderrahman/moviesbymoviedb/repository/MovieRepository.kt#L29) instead of the production one 


<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/EmptyTrendingMovies.png" />

The same can also be done for the [Error Screen](https://github.com/bachiri/MoviesByTheMovieDB-/blob/0a2146852807c96b7390adb7fd883f116043476e/app/src/main/java/io/bachiri/abderrahman/moviesbymoviedb/data/remote/FakeNetworkMovieService.kt#L18) 

<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/ErrorScreen.png" />







## GIF(See Video For Better Quality)
 [Video Link For better quality (Can be opened using Chrome)](https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/MovieByMoviesdB.webm)

<img width="280" src="https://github.com/bachiri/MoviesByTheMovieDB-/blob/main/assets/MovieByMoviesdB.gif" />



## List of Features

- [x] Display Top Rating CTA with a preview of posters
- [x] Display Movie Listing 
- [x] Pagination    
- [x] Pull to refresh
- [x] Sorting by Alphabetical Order or Date


## List Libraries Used 

- Hilt 
- Retrofit
- Glide
- Jetpack ViewModel
- Kotlin Coroutine 
- Kotlin Flow 
- Live Data 
- Swip Refresh Layout
