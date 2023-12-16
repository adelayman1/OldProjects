package com.adel.moviesapp.presentation.savedScreen

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adel.moviesapp.data.model.MovieModel
import com.adel.moviesapp.databinding.FragmentSavedMoviesBinding
import com.adel.moviesapp.domain.enities.Result
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedMoviesFragment : Fragment() {
    lateinit var binding: FragmentSavedMoviesBinding
    lateinit var viewModel: SavedMoviesViewModel
    lateinit var navController: NavController
    var movieList: MutableList<MovieModel> = mutableListOf()
    lateinit var adapter: SavedMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SavedMoviesViewModel::class.java)
        adapter = SavedMoviesAdapter(movieList, ({
            navController.navigate(
                SavedMoviesFragmentDirections.actionSavedMoviesFragmentToMovieDetailsFragment(
                    it
                )
            )
        }))
        binding.rcSavedMovies.adapter = adapter
        binding.rcSavedMovies.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        lifecycleScope.launch(Dispatchers.IO) {
            whenStarted {
                viewModel.savedMovies.collect {
                    when (it) {
                        is Result.Loading -> {
                            binding.lnSavedMovies.visibility = View.GONE
                            binding.loadingProgress.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            movieList.clear()
                            movieList.addAll(it.data)
                            adapter.notifyDataSetChanged()
                            binding.lnSavedMovies.visibility = View.VISIBLE
                            binding.loadingProgress.visibility = View.GONE
                        }
                        is Result.Error -> MDToast.makeText(
                            requireContext(), "error " + it.error.javaClass.simpleName, MDToast.LENGTH_SHORT,
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
        binding = FragmentSavedMoviesBinding.inflate(inflater, container, false)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return binding.root
    }


}