package com.adel.moviesapp.presentation.splashScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adel.moviesapp.databinding.FragmentSplashBinding
import com.adel.moviesapp.domain.enities.Result
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    lateinit var navController: NavController
    lateinit var binding: FragmentSplashBinding
    lateinit var viewModel: SplashViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            whenStarted {
                viewModel.isSplash.collect {
                    when (it) {
                        is Result.Loading -> null
                        is Result.Success -> {
                            if (it.data) {
                                navController.navigate(SplashFragmentDirections.actionSplashFragmentToStartFragment())
                            } else {
                                navController.navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                            }
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
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

}
