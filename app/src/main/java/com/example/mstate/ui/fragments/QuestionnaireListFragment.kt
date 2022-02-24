package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mstate.databinding.FragmentQuestionnaireListBinding

class QuestionnaireListFragment : Fragment() {

    private var _binding: FragmentQuestionnaireListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionnaireListBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    fun init() {
        binding.btnPhq.setOnClickListener {

        }
        binding.btnEpds.setOnClickListener {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}