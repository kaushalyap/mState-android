package com.example.mstate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.mstate.R
import androidx.navigation.ui.setupWithNavController
import com.example.mstate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        binding.fabCheck.setOnClickListener {
            navController.navigate(R.id.action_main_to_questionnaire)
        }
    }

    private fun initNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomAppBar.setupWithNavController(navController)
        setSupportActionBar(binding.bottomAppBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history -> navController.navigate(R.id.action_main_to_history)
            R.id.settings -> navController.navigate(R.id.action_main_to_settings)
            R.id.disclaimer -> navController.navigate(R.id.action_main_to_disclaimer)
            R.id.about -> navController.navigate(R.id.action_main_to_about)
        }
        return true
    }
}