package com.example.mstate.ui.fragments


import android.content.Context
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
import com.example.mstate.services.FirestoreService

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: HistoryAdapter
    private lateinit var firestoreService: FirestoreService

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

        val items: List<HistoryItem> = readTestHistory()

        adapter = HistoryAdapter(items)
        binding.recyclerView.adapter = adapter
    }

    private fun readTestHistory(): List<HistoryItem> {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val docRef = sharedPref?.getString(getString(R.string.pref_user_doc_ref), null)
        firestoreService = FirestoreService()
        val items: List<HistoryItem> = firestoreService.readHistory(docRef!!)
        return items
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}