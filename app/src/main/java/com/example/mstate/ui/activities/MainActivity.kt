package com.example.mstate.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mstate.R
import com.example.mstate.databinding.ActivityMainBinding
import com.example.mstate.models.User
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import permissions.dispatcher.PermissionRequest
import java.util.*




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideFabBottomAppBar()
        binding.fabCheck.setOnClickListener {
            navController.navigate(R.id.action_main_to_questionnaire)
        }

        initNavigation()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {

            navController.navigate(R.id.action_global_signIn)
        }

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null)
            navController.navigate(R.id.action_global_signIn)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    navController.navigate(R.id.action_signIn_to_main)
                    Toast.makeText(
                        applicationContext,
                        "Welcome ${user?.displayName}!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(applicationContext, "Sign In failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
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
            R.id.signOut -> {
                Firebase.auth.signOut()
                navController.navigate(R.id.action_global_signIn)
            }
        }
        return true
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java) ?: return
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken ?: return)

                val user = User(account.displayName ?: return, account.email ?: return, null)
                addUser(user)


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(applicationContext, "Google sign in failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addUser(user: User) {
        firestoreService = FirestoreService()
        firestoreService.addUser(object : UserCallback {
            override fun onCallback(dRef: String) {
                val sharedPref = getPreferences(MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString(getString(R.string.pref_user_doc_ref), dRef)
                    apply()
                }
            }
        }, user)
    }

    internal fun showFabBottomAppBar() {
        binding.fabCheck.show()
        binding.bottomAppBar.performShow()
    }

    internal fun hideFabBottomAppBar() {
        binding.fabCheck.hide()
        binding.bottomAppBar.performHide()
    }

    internal fun showPermissionRationaleDialog(
        @StringRes messageResId: Int,
        request: PermissionRequest
    ) {
        AlertDialog.Builder(this)
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }
}