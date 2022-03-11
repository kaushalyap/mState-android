package com.example.mstate.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignInEmailBinding
import com.example.mstate.models.AppUser
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
                binding.lbError.text = "Email / Password cannot be empty!"
            else
                signInWithEmail(email, password)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val dRef = sharedPreferences.getString(
                        requireContext().resources.getString(R.string.pref_user_doc_ref),
                        ""
                    ).toString()
                    firestoreService.readUser(object : UserCallback {
                        override fun onPostExecute(dRef: String) {}

                        override fun onPostExecute(user: AppUser) {
                            Log.d(TAG, "profileComplete = ${user.profileComplete}")
                            if (user.profileComplete)
                                findNavController().navigate(R.id.action_signInEmail_to_editProfile)
                            else
                                findNavController().navigate(R.id.action_signInEmail_to_main)
                        }
                    }, dRef)
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