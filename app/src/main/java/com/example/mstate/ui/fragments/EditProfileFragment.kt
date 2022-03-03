package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.mstate.R
import com.example.mstate.databinding.FragmentEditProfileBinding
import com.example.mstate.models.AppUser
import com.example.mstate.models.Guardian
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val args: EditProfileFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreService: FirestoreService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        val formName = args.formName
        binding.lbUpdateProfile.text = formName

        firestoreService = FirestoreService()
        auth = Firebase.auth
        val signedInWithGoogle = auth.currentUser?.displayName != null

        if (signedInWithGoogle)
            binding.editName.setText(auth.currentUser?.displayName)

        binding.btnDone.setOnClickListener {

            val fullName = binding.editName.text.toString()
            val email = auth.currentUser?.email
            val address = binding.editAddress.text.toString()
            val mobileNo = binding.editMobileNo.text.toString()
            val guardianName = binding.editGuardian.text.toString()
            val guardianMobileNo = binding.editGuardianMobileNo.text.toString()


            val isValid = fullName.isNotEmpty() and
                    address.isNotEmpty() and
                    mobileNo.isNotEmpty() and
                    guardianName.isNotEmpty() and
                    guardianMobileNo.isNotEmpty()

            if (isValid) {
                val guardian = Guardian(guardianName, guardianMobileNo)
                val user = AppUser(
                    fullName,
                    email ?: return@setOnClickListener, address, mobileNo, guardian, null
                )

                if (formName == "Create Profile") {
                    firestoreService.addUser(object : UserCallback {
                        @SuppressLint("LogConditional")
                        override fun onCallback(dRef: String) {
                            val sharedPref =
                                activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                            with(sharedPref.edit()) {
                                putString(
                                    getString(R.string.pref_user_doc_ref),
                                    dRef
                                )
                                apply()
                            }
                            Log.d(TAG, "dRef = $dRef")
                            findNavController().navigate(R.id.action_editProfile_to_main)
                        }
                    }, user)
                } else {
                    val sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val dRef = sharedPreferences.getString(
                        requireContext().resources.getString(R.string.pref_user_doc_ref),
                        ""
                    ).toString()
                    firestoreService.updateUser(dRef, user)
                    findNavController().popBackStack()
                }
            } else
                binding.lbError.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "EditProfileFragment"
    }
}