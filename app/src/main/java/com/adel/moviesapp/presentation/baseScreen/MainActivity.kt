package com.adel.moviesapp.presentation.baseScreen

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.adel.moviesapp.R
import com.adel.moviesapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.OnItemSelectedListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment:NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_bar)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.homeFragment -> binding.bottomBar.visibility= View.VISIBLE
                R.id.savedMoviesFragment ->binding.bottomBar.visibility= View.VISIBLE
                R.id.profileFragment ->binding.bottomBar.visibility= View.VISIBLE
                else -> binding.bottomBar.visibility= View.GONE
            }
        }
    }
}