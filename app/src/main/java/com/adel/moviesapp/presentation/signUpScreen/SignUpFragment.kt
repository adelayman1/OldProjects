package com.adel.moviesapp.presentation.signUpScreen

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
import com.adel.moviesapp.databinding.FragmentSignUpBinding
import com.adel.moviesapp.domain.enities.Result
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var binding: FragmentSignUpBinding
    lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.btnSignUp.setOnClickListener {
            if (binding.etUsername.text.toString().trim().isEmpty()) {
                binding.etUsername.error = "please type user name"
                return@setOnClickListener
            }
            if (binding.etEmail.text.toString().trim().isEmpty()) {
                binding.etEmail.error = "please type email"
                return@setOnClickListener
            }
            if (binding.etPassword.text.toString().trim().isEmpty()) {
                binding.etPassword.error = "please type password"
                return@setOnClickListener
            }

            viewModel.signUp(
                binding.etUsername.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
        lifecycleScope.launch(Dispatchers.IO) {
            whenStarted {
                viewModel.isSignUpSuccess.collect {
                    when (it) {
                        is Result.Loading -> null
                        is Result.Success -> navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

}