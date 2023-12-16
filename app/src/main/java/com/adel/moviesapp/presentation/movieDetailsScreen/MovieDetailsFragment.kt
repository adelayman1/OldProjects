package com.adel.moviesapp.presentation.movieDetailsScreen

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import com.adel.moviesapp.R
import com.adel.moviesapp.databinding.FragmentMovieDetailsBinding
import com.adel.moviesapp.domain.enities.Result
import com.squareup.picasso.Picasso
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    lateinit var navController: NavController
    val args: MovieDetailsFragmentArgs by navArgs()
    lateinit var viewModel: MovieDetailsViewModel
    lateinit var binding: FragmentMovieDetailsBinding
    var NOT_SAVED: Int = 0
    var SAVED: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieDetailsViewModel::class.java)
        navController = findNavController()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(args.movieModel.image).into(binding.ivFilm)
        with(args.movieModel) {
            binding.tvMovieName.text = name
            binding.tvMovieType.text = type
            binding.tvRuntime.text = "runtime:$runtime"
            binding.prgRate.progress = rate.toInt()
            binding.tvMovieRate.text = rate.toString()
            binding.tvDescription.text = description
        }
        binding.tvDescription.movementMethod = ScrollingMovementMethod()
        binding.ivBack.setOnClickListener {
            navController.navigateUp()
        }
        binding.ivSave.setOnClickListener {
            if (binding.ivSave.tag == SAVED) {
                viewModel.unSaveMovie(args.movieModel.key)
            } else if (binding.ivSave.tag == NOT_SAVED) {
                viewModel.saveMovie(args.movieModel.key)
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            whenStarted {
                viewModel.isMovieSaved.collect {
                    when (it) {
                        is Result.Loading -> null
                        is Result.Success -> {
                            if (it.data) {
                                binding.ivSave.setImageResource(R.drawable.ic_baseline_bookmark_24)
                                binding.ivSave.tag = SAVED
                            }else {
                                binding.ivSave.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                                binding.ivSave.tag = NOT_SAVED
                            }
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
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


}