package com.example.mstate.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mstate.R
import com.example.mstate.databinding.FragmentEditProfileBinding
import com.example.mstate.models.AppUser
import com.example.mstate.models.Guardian
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("LogConditional")
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    internal val binding get() = _binding!!
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

        if (formName == "Update Profile") {
            firestoreService.readUser(object : UserCallback {
                override fun onPostExecute(dRef: String) {}

                override fun onPostExecute(user: AppUser) {
                    binding.editName.setText(user.name)
                    binding.editAddress.setText(user.address)
                    binding.editMobileNo.setText(user.mobileNo)
                    binding.editGuardian.setText(user.guardian?.fullName)
                    binding.editGuardianMobileNo.setText(user.guardian?.mobileNo)
                }
            }, auth.currentUser?.uid.toString())
        }

        binding.btnDone.setOnClickListener {

            val uid = auth.currentUser?.uid.toString()
            val fullName = binding.editName.text.toString().trim()
            val email = auth.currentUser?.email.toString()
            val address = binding.editAddress.text.toString().trim()
            val mobileNo = binding.editMobileNo.text.toString()
            val guardianName = binding.editGuardian.text.toString().trim()
            val guardianMobileNo = binding.editGuardianMobileNo.text.toString()

            val isValid = fullName.isNotEmpty() and
                    address.isNotEmpty() and
                    mobileNo.isNotEmpty() and
                    guardianName.isNotEmpty() and
                    guardianMobileNo.isNotEmpty()

            if (isValid) {
                if (mobileNo != guardianMobileNo) {
                    val guardian = Guardian(guardianName, guardianMobileNo)
                    val user = AppUser(
                        uid,
                        fullName,
                        email, true, address, mobileNo, guardian, null
                    )

                    firestoreService.updateUser(
                        uid,
                        user
                    )
                } else {
                    binding.lbError.text = resources.getString(R.string.mobile_no_cannot_be_same)
                    binding.lbError.visibility = View.VISIBLE
                }

                if (formName == resources.getString(R.string.create_profile))
                    findNavController().navigate(R.id.action_editProfile_to_main)
                else
                    findNavController().popBackStack()
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