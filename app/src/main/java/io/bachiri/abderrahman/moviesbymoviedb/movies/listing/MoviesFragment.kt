package io.bachiri.abderrahman.moviesbymoviedb.movies.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.bachiri.abderrahman.moviesbymoviedb.R
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.databinding.FragmentMoviesBinding
import io.bachiri.abderrahman.moviesbymoviedb.movies.MoviesViewModel
import io.bachiri.abderrahman.moviesbymoviedb.movies.ViewState

@AndroidEntryPoint
class MoviesFragment : Fragment() {
    companion object {
        const val NUMBER_OF_ITEMS_TO_TRIGGER_PAGINATION = 2;

        fun getFragmentTag(): String {
            return "MoviesFragment"
        }
    }

    private lateinit var fragmentMovieBinding: FragmentMoviesBinding
    private val moviesViewModel: MoviesViewModel by activityViewModels()

    private var errorSnackBar: Snackbar? = null
    private var loading = true
    private var lastVisibleScrollViewItem = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMovieBinding = FragmentMoviesBinding.inflate(layoutInflater)
        return fragmentMovieBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MoviesAdapter()

        initRecyclerview(adapter)
        manageViewModel(adapter)
        managePagination()
        manageRefreshLayout()
        manageSorting()

    }

    private fun manageSorting() {
        fragmentMovieBinding.sortingAlphabetical.setOnClickListener {
            moviesViewModel.setSortingType(MoviesViewModel.Sorting.ALPHABETICAl)
        }
        fragmentMovieBinding.sortingNormal.setOnClickListener {
            moviesViewModel.setSortingType(MoviesViewModel.Sorting.NORMAL)
        }
        fragmentMovieBinding.sortingChronogical.setOnClickListener {
            moviesViewModel.setSortingType(MoviesViewModel.Sorting.CHRONOLOGICAL)
        }
    }

    private fun manageViewModel(adapter: MoviesAdapter) {
        moviesViewModel
            .movies
            .asLiveData()
            .observe(viewLifecycleOwner, {
                manageListingState(it, adapter)
                manageLoadingState(it)
                manageEmptyDataState(it)
                manageErrorState(it)
            })
    }

    private fun initRecyclerview(adapter: MoviesAdapter) {
        fragmentMovieBinding.moviesRecyclerView.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun manageRefreshLayout() {
        fragmentMovieBinding.refresherLayout.setOnRefreshListener {
            moviesViewModel.refreshData()
        }
    }

    private fun managePagination() {
        fragmentMovieBinding.moviesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0) {
                    recyclerView.layoutManager?.let {

                        //pastvisible itmes
                        val pastVisibleItems =
                            (it as LinearLayoutManager).findFirstVisibleItemPosition()
                        //items
                        val totalItemCount = it.itemCount

                        //first visible item
                        val visibleItemCount = it.childCount
                        if (loading) {
                            if (pastVisibleItems + visibleItemCount > totalItemCount - NUMBER_OF_ITEMS_TO_TRIGGER_PAGINATION) {
                                lastVisibleScrollViewItem = pastVisibleItems
                                loading = false
                                moviesViewModel.loadMore()
                                Toast.makeText(
                                    requireContext(),
                                    "result is loading",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun manageListingState(viewState: ViewState<List<Movie>>, adapter: MoviesAdapter) {
        adapter.submitList(viewState.data)
        if (viewState.data?.size ?: -1 > 0) {
            fragmentMovieBinding.moviesRecyclerView.scrollToPosition(lastVisibleScrollViewItem) //Start the scroll
        }

    }

    private fun manageEmptyDataState(it: ViewState<List<Movie>>) {
        fragmentMovieBinding.emptyDataView.visibility =
            if (it.showEmptyData) View.VISIBLE else View.GONE
        fragmentMovieBinding.emptyDataIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_empty_movie
            )
        )
        fragmentMovieBinding.emptyDataLabel.text = getString(R.string.movies_empty_data_label)

    }

    private fun manageLoadingState(it: ViewState<List<Movie>>) {
        fragmentMovieBinding.progressBar.visibility = if (it.loading) View.VISIBLE else View.GONE
        loading = it.resultLoaded
        fragmentMovieBinding.refresherLayout.isRefreshing = !it.resultLoaded
    }

    private fun manageErrorState(it: ViewState<List<Movie>>) {
        fragmentMovieBinding.errorView.visibility =
            if (it.errorMessage.isNotEmpty()) View.VISIBLE else View.GONE
        fragmentMovieBinding.errorIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_error
            )
        )
        fragmentMovieBinding.errorLabel.text = getString(R.string.movies_error_label)

        if (it.errorMessage.isNotEmpty()) {
            errorSnackBar?.dismiss()
            errorSnackBar = Snackbar.make(
                fragmentMovieBinding.root,
                it.errorMessage, Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.movie_error_retry) { moviesViewModel.refreshData() }
                .also { it.show() }
        }
    }


}