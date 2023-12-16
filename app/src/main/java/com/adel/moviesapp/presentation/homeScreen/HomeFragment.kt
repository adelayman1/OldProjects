package com.adel.moviesapp.presentation.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.databinding.FragmentHomeBinding
import com.adel.moviesapp.domain.enities.Result
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var binding: FragmentHomeBinding
    var allMovies: MutableList<MovieModel> = mutableListOf()
    var popularMovies: MutableList<MovieModel> = mutableListOf()
    lateinit var allMoviesAdapter: AllMoviesAdapter
    lateinit var popularMoviesAdapter: PopularMoviesSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: HomeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        allMoviesAdapter = AllMoviesAdapter(allMovies, ({
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(
                    it
                )
            )
        }))
        popularMoviesAdapter = PopularMoviesSliderAdapter(popularMovies, ({ movieModel ->

            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(
                    movieModel
                )
            )
        }))
        binding.recentMoviesViewPager.adapter = popularMoviesAdapter
        binding.rcMovies.adapter = allMoviesAdapter
        binding.rcMovies.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.recentMoviesViewPager.clipChildren = false
        binding.recentMoviesViewPager.clipToPadding = false
        binding.recentMoviesViewPager.offscreenPageLimit = 3
        binding.recentMoviesViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.recentMoviesViewPager.setPageTransformer(transformer)
        lifecycleScope.launch(Dispatchers.IO) {
            whenStarted {
                viewModel.allMovies.collect {
                    when (it) {
                        is Result.Loading -> {
                            binding.svMain.visibility = View.GONE
                            binding.loadingProgress.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            allMovies.clear()
                            allMovies.addAll(it.data!!)
                            allMoviesAdapter.notifyDataSetChanged()
                            popularMovies.clear()
                            popularMovies.addAll(it.data!!)
                            popularMoviesAdapter.notifyDataSetChanged()
                            binding.svMain.visibility = View.VISIBLE
                            binding.loadingProgress.visibility = View.GONE
                        }
                        is Result.Error -> MDToast.makeText(
                            requireContext(),
                            "error " + it.error.javaClass.simpleName,
                            MDToast.LENGTH_SHORT,
                            MDToast.TYPE_ERROR
                        ).show()
                    }
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        return binding.root
    }


}