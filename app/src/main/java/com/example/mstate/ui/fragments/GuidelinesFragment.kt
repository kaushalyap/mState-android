package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.databinding.FragmentGuidelinesBinding
import com.example.mstate.models.AutomatedResponse

class GuidelinesFragment : Fragment() {

    private var _binding: FragmentGuidelinesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidelinesBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.btn.setOnClickListener {
            findNavController().popBackStack()
            val automatedResponse = AutomatedResponse()
            automatedResponse.respond(requireActivity(), requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}