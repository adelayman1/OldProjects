package com.adel.moviesapp.presentation.startScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adel.moviesapp.databinding.FragmentStartBinding


class StartFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var binding: FragmentStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSignIn.setOnClickListener { navController.navigate(StartFragmentDirections.actionStartFragmentToLoginFragment()) }
        binding.btnSignup.setOnClickListener { navController.navigate(StartFragmentDirections.actionStartFragmentToSignUpFragment()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

}