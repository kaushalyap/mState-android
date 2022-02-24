package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mstate.databinding.FragmentMainBinding
import com.example.mstate.ui.activities.MainActivity


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        mainActivity = activity as MainActivity
        mainActivity.showFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivity.hideFabBottomAppBar()
    }
}