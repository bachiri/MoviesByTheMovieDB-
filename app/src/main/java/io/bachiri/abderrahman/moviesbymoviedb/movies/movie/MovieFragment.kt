package io.bachiri.abderrahman.moviesbymoviedb.movies.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import io.bachiri.abderrahman.moviesbymoviedb.databinding.FragmentMovieBinding
import io.bachiri.abderrahman.moviesbymoviedb.movies.MoviesViewModel
import io.bachiri.abderrahman.moviesbymoviedb.utils.GlideBlurTransformation
import io.bachiri.abderrahman.moviesbymoviedb.utils.UtilsPicture

class MovieFragment : Fragment() {

    companion object {

        fun getFragmentTag(): String {
            return "MovieFragment"
        }
    }

    private lateinit var fragmentMovieBinding: FragmentMovieBinding
    private val movieViewModel: MoviesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMovieBinding = FragmentMovieBinding.inflate(layoutInflater)
        return fragmentMovieBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageViewModel()

    }

    private fun manageViewModel() {
        movieViewModel
            .currentMovie
            .asLiveData()
            .observe(viewLifecycleOwner, {
                manageMovieState(it)
            })

    }

    private fun manageMovieState(movieState: MoviesViewModel.MovieViewState) {
        val movie = movieState.data

        movie?.let {
            fragmentMovieBinding.movieContainer.transitionName = it.id.toString()
            fragmentMovieBinding.movieTitle.text = it.movieName
            fragmentMovieBinding.movieDescription.text = it.overview
            fragmentMovieBinding.movieRatingCountLabel.text = it.voteCount.toString()
            fragmentMovieBinding.movieRatingLabel.text = it.voteAverage.toString()

            it.moviePosterPath?.let {
                Glide.with(fragmentMovieBinding.moviePoster)
                    .asBitmap()
                    .load(UtilsPicture.getCompleteUrlFrom(it))
                    .into(fragmentMovieBinding.moviePoster)


                Glide.with(fragmentMovieBinding.movieBlurredBackGround)
                    .asBitmap()
                    .transform(GlideBlurTransformation(fragmentMovieBinding.movieBlurredBackGround.context))
                    .load(UtilsPicture.getCompleteUrlFrom(it))
                    .into(fragmentMovieBinding.movieBlurredBackGround)
            }
        }
    }
}