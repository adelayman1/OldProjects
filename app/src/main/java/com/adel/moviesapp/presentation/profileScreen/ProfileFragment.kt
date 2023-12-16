package com.adel.moviesapp.presentation.profileScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adel.moviesapp.databinding.FragmentProfileBinding
import com.adel.moviesapp.domain.enities.Result
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var viewModel: ProfileViewModel
    lateinit var binding: FragmentProfileBinding
    var name: String? = null
    var oldPassword: String? = null
    var newPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.userName.collect {
                when (it) {
                    is Result.Loading -> null
                    is Result.Success -> {
                        binding.etName.setText(it.data)
                    }
                    is Result.Error -> MDToast.makeText(
                            requireContext(), "error " + it.error.javaClass.simpleName, MDToast.LENGTH_SHORT,
                            MDToast.TYPE_ERROR
                    ).show()
                }
            }
        }
        binding.etName.doOnTextChanged { text, start, before, count ->
            name = text.toString().trim()
        }

        binding.etOldPassword.doOnTextChanged { text, start, before, count ->
            oldPassword = text.toString().trim()
        }

        binding.etNewPassword.doOnTextChanged { text, start, before, count ->
            newPassword = text.toString().trim()
        }
        binding.btnUpdate.setOnClickListener {
            viewModel.update(name, oldPassword, newPassword)
        }
     }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}
