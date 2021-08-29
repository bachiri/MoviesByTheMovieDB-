package io.bachiri.abderrahman.moviesbymoviedb.movies.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.databinding.ItemViewMovieBinding
import io.bachiri.abderrahman.moviesbymoviedb.utils.UtilsPicture

class MoviesAdapter(private val onMovieClicked: (id: Int) -> Unit) :
    RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    var movies: MutableList<Movie> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemViewMovieBinding.inflate(LayoutInflater.from(parent.context))
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position], onMovieClicked)
    }

    fun addMovies(movies: List<Movie>) {
        val difference = movies.minus(this.movies)
        this.movies.addAll(difference)
        notifyDataSetChanged()
    }

    fun replaceMovies(movies: List<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    class MovieViewHolder(private val binding: ItemViewMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, onMovieClicked: (id: Int) -> Unit) {
            binding.movieTitle.text = movie.movieName
            binding.movieContainer.setOnClickListener {
                onMovieClicked.invoke(movie.id)
            }
            ViewCompat.setTransitionName(binding.movieContainer, movie.id.toString())
            binding.movieRatingCountLabel.text = movie.voteCount.toString()
            binding.movieRatingLabel.text = movie.voteAverage.toString()

            movie.moviePosterPath?.let {
                Glide.with(binding.moviePoster)
                    .asBitmap()
                    .load(UtilsPicture.getCompleteUrlFrom(it))
                    .into(binding.moviePoster)
            }
        }

    }
}