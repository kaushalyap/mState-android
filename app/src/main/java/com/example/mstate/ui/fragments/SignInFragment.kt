package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.mstate.R
import com.example.mstate.databinding.FragmentSignInBinding
import com.example.mstate.ui.activities.MainActivity


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        mainActivity = activity as MainActivity
        binding.btnGoogleSignIn.setOnClickListener {
            mainActivity.signInWithGoogle()
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
            mainActivity.signInWithEmail(email, password)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.create().show()
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
                mainActivity.signUpWithEmail(email, password)
                Log.d(TAG, "Email : $email, Password : $password")
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "SignInFragment"
    }
}