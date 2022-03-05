package com.example.mstate.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignInEmailBinding
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInEmailFragment : Fragment() {

    private var _binding: FragmentSignInEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreService: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInEmailBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        firestoreService = FirestoreService()
        binding.btnSignIn.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            if (email.isEmpty() || password.isEmpty())
                signInWithEmail(email, password)
            else
                binding.lbError.text = "Email / Password cannot be empty!"
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
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
                    Log.w(SignInFragment.TAG, "signInWithEmail:failure", task.exception)
                    binding.lbError.visibility = View.VISIBLE
                    binding.lbError.text = "Incorrect email / password"
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "SignInEmailFragment"
    }
}