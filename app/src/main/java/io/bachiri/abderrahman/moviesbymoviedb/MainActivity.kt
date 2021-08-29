package io.bachiri.abderrahman.moviesbymoviedb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.bachiri.abderrahman.moviesbymoviedb.movies.home.HomeFragment
import io.bachiri.abderrahman.moviesbymoviedb.movies.listing.MoviesFragment
import io.bachiri.abderrahman.moviesbymoviedb.movies.movie.MovieFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
        }

    }

    fun navigateToTopRatedTvShows() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MoviesFragment())
            .addToBackStack(MoviesFragment.getFragmentTag())
            .commitAllowingStateLoss()
    }

    fun navigateMovieView() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MovieFragment())
            .addToBackStack(MovieFragment.getFragmentTag())
            .commitAllowingStateLoss()
    }
}