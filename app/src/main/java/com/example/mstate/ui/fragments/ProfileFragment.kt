package com.example.mstate.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.databinding.FragmentProfileBinding
import com.example.mstate.models.AppUser
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.UserCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    internal val binding get() = _binding!!
    private lateinit var firestoreService: FirestoreService
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setLabels()

        binding.btnEdit.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileToEditProfile(
                "Update Profile",
            )
            findNavController().navigate(action)
        }
    }

    private fun setLabels() {
        firestoreService = FirestoreService()
        auth = Firebase.auth
        firestoreService.readUser(object : UserCallback {
            override fun onPostExecute(dRef: String) {}

            override fun onPostExecute(user: AppUser) {
                Log.d(TAG, "$user")
                binding.lbName.text = user.name
                binding.lbAddress.text = user.address
                binding.lbMobileNo.text = user.mobileNo
                binding.lbGuardian.text = user.guardian?.fullName
                binding.lbGuardianMobileNo.text = user.guardian?.mobileNo
            }
        }, auth.currentUser?.uid.toString())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "ProfileFragment"
    }
}