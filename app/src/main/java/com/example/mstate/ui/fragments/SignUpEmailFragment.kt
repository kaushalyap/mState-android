package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignUpEmailBinding
import com.example.mstate.models.AppUser
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("LogConditional")
class SignUpEmailFragment : Fragment() {

    private var _binding: FragmentSignUpEmailBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpEmailBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val confirmPassword = binding.editConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                binding.lbError.visibility = View.VISIBLE
                binding.lbError.text = resources.getString(R.string.fill_all_fields)
            } else if (password != confirmPassword) {
                binding.lbError.visibility = View.VISIBLE
                binding.lbError.text = resources.getString(R.string.passwords_do_not_match)
            } else
                signUpWithEmail(email, password)
        }
    }

    private fun signUpWithEmail(email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(SignInFragment.TAG, "createUserWithEmail:success")
                    val firestoreService = FirestoreService()
                    val user = AppUser(
                        auth.currentUser?.uid,
                        auth.currentUser?.displayName,
                        auth.currentUser?.email ?: return@addOnCompleteListener,
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
                    findNavController().navigate(R.id.action_global_signIn)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    binding.lbError.visibility = View.VISIBLE
                    if (task.exception.toString().contains("email address is already in use"))
                        Snackbar.make(
                            binding.root,
                            "Email address is already in use",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    else
                        Snackbar.make(binding.root, "No internet!", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "SignUpEmailFragment"
    }
}