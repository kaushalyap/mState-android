package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignInBinding
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.example.mstate.ui.activities.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
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
            showSignInDialog()
        }
        binding.lbSignUp.setOnClickListener {
            showSignUpDialog()
        }
    }

    @SuppressLint("LogConditional")
    private fun showSignInDialog() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sign In")
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_email_sign_in, null)
        builder.setView(dialogView)
        val editEmail = dialogView.findViewById<EditText>(R.id.edit_email)
        val editPassword = dialogView.findViewById<EditText>(R.id.edit_password)

        builder.setPositiveButton("OK") { _, _ ->
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            signInWithEmail(email, password)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    private fun signInWithEmail(email: String, password: String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val signedInEmail = auth.currentUser?.email
                    firestoreService = FirestoreService()
                    firestoreService.doesUserAlreadyExists(object : UserCallback {
                        override fun onCallback(dRef: String) {
                            Log.d(TAG, "dRef = $dRef")
                            if (dRef.isEmpty())
                                findNavController().navigate(R.id.action_signIn_to_editProfile)
                            else
                                findNavController().navigate(R.id.action_signIn_to_main)
                        }
                    }, signedInEmail!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(requireContext(), "Signed Up!", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Sign Up failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showSignUpDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sign Up")
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_email_sign_up, null)
        builder.setView(dialogView)
        val editEmail = dialogView.findViewById<EditText>(R.id.edit_email)
        val editPassword = dialogView.findViewById<EditText>(R.id.edit_password)
        val editConfirmPassword = dialogView.findViewById<EditText>(R.id.edit_confirm_password)
        val lbError = dialogView.findViewById<TextView>(R.id.lbError)

        builder.setPositiveButton("OK") { _, _ ->
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val confirmPassword = editConfirmPassword.text.toString()
            if (password != confirmPassword) {
                lbError.visibility = View.VISIBLE
            } else {
                signUpWithEmail(email, password)
                Log.d(TAG, "Email : $email, Password : $password")
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.create().show()
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java) ?: return
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken ?: return)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google sign in failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    firestoreService.doesUserAlreadyExists(object : UserCallback {
                        override fun onCallback(dRef: String) {
                            if (dRef.isEmpty()) {
                                findNavController().navigate(R.id.action_signIn_to_editProfile)
                            } else {
                                findNavController().navigate(R.id.action_signIn_to_main)
                                Toast.makeText(
                                    requireContext(),
                                    "Welcome ${user?.displayName}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }, user?.email!!)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Sign In failed!", Toast.LENGTH_SHORT).show()
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