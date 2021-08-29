package io.bachiri.abderrahman.moviesbymoviedb.movies.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.bachiri.abderrahman.moviesbymoviedb.MainActivity
import io.bachiri.abderrahman.moviesbymoviedb.databinding.FragmentMainBinding
import io.bachiri.abderrahman.moviesbymoviedb.movies.MoviesViewModel


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var fragmentMainBinding: FragmentMainBinding
    private val moviesViewModel: MoviesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)
        return fragmentMainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        manageViewModel()
        manageCtaClick()

    }


    private fun manageViewModel() {
        moviesViewModel
            .moviesFirstPictures
            .asLiveData()
            .observe(viewLifecycleOwner, {
                setPicturesForTopTradingCTA(it)
            })
    }

    private fun manageCtaClick() {
        fragmentMainBinding.containerTopRatedCTA.setOnClickListener {
            (requireActivity() as MainActivity).navigateToTopRatedTvShows()
        }
    }

    private fun setPicturesForTopTradingCTA(viewState: MoviesViewModel.MoviesViewState<String>) {
        viewState.data.let {
            if (it.isNotEmpty()) {
                val picturesUrlSize = it.size

                val childCount = fragmentMainBinding.moviePostersContainer.childCount
                for (i in 0 until childCount) {
                    val index =
                        i % picturesUrlSize //This condition is to display again the tv Show posters in case we have less then 5 top shows
                    val currentImageView = fragmentMainBinding.moviePostersContainer.getChildAt(i)
                    Glide.with(currentImageView)
                        .load(it[index])
                        .into(currentImageView as ImageView)
                }
            }

        }
    }

}