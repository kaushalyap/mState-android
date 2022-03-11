package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mstate.R
import com.example.mstate.databinding.FragmentDepressedBinding

class DepressedFragment : Fragment() {

    private var _binding: FragmentDepressedBinding? = null
    private val binding get() = _binding!!
    private val args: DepressedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepressedBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        val result = args.result
        val testType = args.testType
        binding.txtDesc.text =
            String.format(resources.getString(R.string.sorry_to_let_you_know), result, testType)
        binding.btnSeeGuide.setOnClickListener {
            findNavController().navigate(R.id.action_depressed_to_guidelines)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}