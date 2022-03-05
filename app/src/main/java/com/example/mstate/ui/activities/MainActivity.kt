package com.example.mstate.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mstate.R
import com.example.mstate.databinding.ActivityMainBinding
import com.example.mstate.services.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestoreService = FirestoreService()
        binding.fabCheck.setOnClickListener {
            navController.navigate(R.id.action_main_to_questionnaire)
        }

        initNavigation()

        // Navigate to SigIn if not already authenticated
        auth = Firebase.auth
        if (auth.currentUser?.email == null) {
            navController.navigate(R.id.action_global_signIn)
        }
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomAppBar.setupWithNavController(navController)
        setSupportActionBar(binding.bottomAppBar)
        hideFabBottomAppBar()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null)
            navController.navigate(R.id.action_global_signIn)
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
            R.id.signOut -> {
                Firebase.auth.signOut()
                navController.navigate(R.id.action_global_signIn)
            }
        }
        return true
    }

    internal fun showFabBottomAppBar() {
        binding.fabCheck.show()
        binding.bottomAppBar.performShow()
    }

    internal fun hideFabBottomAppBar() {
        binding.fabCheck.hide()
        binding.bottomAppBar.performHide()
    }

    companion object {
        private const val TAG: String = "MainActivity"
    }
}