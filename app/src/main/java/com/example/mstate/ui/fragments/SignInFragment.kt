package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignInBinding
import com.example.mstate.models.AppUser
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@SuppressLint("LogConditional")
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    internal val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestoreService: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setupGoogleSignInClient()
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
        binding.btnEmailSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signInEmail)
        }
        binding.lbSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signIn_to_signUpEmail)
        }
    }

    private fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        GoogleSignIn.getLastSignedInAccount(requireContext())
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java) ?: return
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken ?: return)

            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Snackbar.make(binding.root, "Google sign in failed!", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = AppUser(
                        auth.currentUser?.uid,
                        auth.currentUser?.displayName,
                        auth.currentUser?.email.toString(),
                        false,
                        null,
                        null,
                        null,
                        null
                    )
                    firestoreService.addUser(object : UserCallback {

                        override fun onPostExecute(dRef: String) {
                            Log.d(TAG, "dRef = $dRef")
                        }

                        override fun onPostExecute(user: AppUser) {}
                    }, user)

                    val uid = auth.uid
                    firestoreService.readUser(object : UserCallback {
                        override fun onPostExecute(dRef: String) {}

                        override fun onPostExecute(user: AppUser) {
                            if (!user.profileComplete) {
                                findNavController().navigate(R.id.action_signIn_to_editProfile)
                            } else {
                                findNavController().navigate(R.id.action_signIn_to_main)
                                Snackbar.make(
                                    binding.root,
                                    "Welcome ${user.name}!",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, uid.toString())

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Snackbar.make(binding.root, R.string.sign_in_failed, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "SignInFragment"
        const val RC_SIGN_IN: Int = 9001
    }
}