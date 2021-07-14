package io.bachiri.abderrahman.moviesbymoviedb.movies.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.bachiri.abderrahman.moviesbymoviedb.UtilsPicture
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.databinding.ItemViewMovieBinding

class MoviesAdapter : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemViewMovieBinding.inflate(LayoutInflater.from(parent.context))
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieDiff : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }

    class MovieViewHolder(private val binding: ItemViewMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.movieName
            binding.movieRatingCountLabel.text = movie.voteCount.toString()
            binding.movieRatingLabel.text = movie.voteAverage.toString()

            movie.moviePosterPath?.let {
                Glide.with(binding.moviePoster)
                    .load(UtilsPicture.getCompleteUrlFrom(it))
                    .into(binding.moviePoster)
            }


        }

    }


}