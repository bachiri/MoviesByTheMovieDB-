package io.bachiri.abderrahman.moviesbymoviedb.movies.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.bachiri.abderrahman.moviesbymoviedb.MainActivity
import io.bachiri.abderrahman.moviesbymoviedb.R
import io.bachiri.abderrahman.moviesbymoviedb.data.Movie
import io.bachiri.abderrahman.moviesbymoviedb.databinding.FragmentMoviesBinding
import io.bachiri.abderrahman.moviesbymoviedb.movies.MoviesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MoviesFragment : Fragment() {
    companion object {
        const val NUMBER_OF_ITEMS_TO_TRIGGER_PAGINATION = 10

        fun getFragmentTag(): String {
            return "MoviesFragment"
        }
    }

    private lateinit var fragmentMovieBinding: FragmentMoviesBinding
    private val moviesViewModel: MoviesViewModel by activityViewModels()

    private var errorSnackBar: Snackbar? = null
    private var loading = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manageMoviesEffects()
    }

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

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        val adapter = buildAdapter()
        initRecyclerview(adapter)
        manageViewModel(adapter)
        managePagination()
        manageRefreshLayout()
        manageSorting()

    }

    private fun manageMoviesEffects() {
        lifecycleScope.launchWhenStarted {
            moviesViewModel.effects.collect {
                when (it) {
                    is MoviesViewModel.MoviesViewEffect.MoviesOpenMovieView -> {
                        (this@MoviesFragment.requireActivity() as MainActivity).navigateMovieView()
                    }
                }
            }
        }
    }

    private fun buildAdapter(): MoviesAdapter {
        val moviesAdapter = MoviesAdapter {
            lifecycleScope.launchWhenStarted {
                moviesViewModel.intents.send(
                    MoviesViewModel.MoviesIntent.MoviesOnMovieClicked(it)
                )
            }
        }
        (moviesAdapter as RecyclerView.Adapter<*>).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        return moviesAdapter
    }

    private fun manageSorting() {

        fragmentMovieBinding.sortingAlphabetical.setOnClickListener {
            sortBy(MoviesViewModel.Sorting.ALPHABETICAl)

        }
        fragmentMovieBinding.sortingNormal.setOnClickListener {
            sortBy(MoviesViewModel.Sorting.NORMAL)
        }

        fragmentMovieBinding.sortingChronogical.setOnClickListener {
            sortBy(MoviesViewModel.Sorting.CHRONOLOGICAL)
        }
    }

    private fun sortBy(sortingType: MoviesViewModel.Sorting): Job {
        val intent = MoviesViewModel.MoviesIntent.MoviesSortingIntent(sortingType)
        return sendMovieIntent(intent)
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
            sendMovieIntent(MoviesViewModel.MoviesIntent.MoviesRefresh)
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
                                loading = false
                                sendMovieIntent(MoviesViewModel.MoviesIntent.MoviesLoadMore)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun manageListingState(
        viewState: MoviesViewModel.MoviesViewState<Movie>,
        adapter: MoviesAdapter
    ) {
        if (viewState.sortingApplied) {
            adapter.replaceMovies(viewState.data)
        } else {
            adapter.addMovies(viewState.data)
        }
    }

    private fun manageEmptyDataState(it: MoviesViewModel.MoviesViewState<Movie>) {
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

    private fun manageLoadingState(it: MoviesViewModel.MoviesViewState<Movie>) {
        fragmentMovieBinding.progressBar.visibility = if (it.loading) View.VISIBLE else View.GONE
        loading = it.resultLoaded
        fragmentMovieBinding.refresherLayout.isRefreshing = !it.resultLoaded
    }

    private fun manageErrorState(it: MoviesViewModel.MoviesViewState<Movie>) {
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
                .setAction(R.string.movie_error_retry) {
                    sendMovieIntent(MoviesViewModel.MoviesIntent.MoviesRefresh)
                }
                .also { it.show() }
        }
    }

    private fun sendMovieIntent(intent: MoviesViewModel.MoviesIntent) =
        lifecycleScope.launchWhenStarted {
            moviesViewModel.intents.send(
                intent
            )
        }


}