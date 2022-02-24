package com.example.mstate.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mstate.R
import com.example.mstate.adapters.HistoryAdapter
import com.example.mstate.databinding.FragmentHistoryBinding
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.QuestionnaireType

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager
        val items: Array<HistoryItem> = arrayOf(
            HistoryItem(
                "12 of Jan 2022",
                "2.55 PM",
                QuestionnaireType.EPDS.qType,
                R.drawable.ic_about_24,
                2.0f
            )
        )

        adapter = HistoryAdapter(items)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}