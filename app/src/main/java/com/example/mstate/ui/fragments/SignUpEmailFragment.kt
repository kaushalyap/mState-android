package com.example.mstate.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignUpEmailBinding
import com.example.mstate.models.AppUser
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
                binding.lbError.text = "Please fill all the fields"
            } else if (password != confirmPassword) {
                binding.lbError.visibility = View.VISIBLE
                binding.lbError.text = "Passwords do not match!"
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
                            val sharedPref =
                                activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                            with(sharedPref.edit()) {
                                putString(
                                    getString(R.string.pref_user_doc_ref),
                                    dRef
                                )
                                apply()
                                Toast.makeText(requireContext(), "Signed Up!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            Log.d(EditProfileFragment.TAG, "dRef = $dRef")
                        }

                        override fun onPostExecute(user: AppUser) {}

                    }, user)
                    findNavController().navigate(R.id.action_global_signIn)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    binding.lbError.visibility = View.VISIBLE
                    var errorMessage = ""
                    errorMessage =
                        if (task.exception.toString().contains("email address is already in use"))
                            "Email address is already in use"
                        else
                            "No internet!"
                    binding.lbError.text = errorMessage
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