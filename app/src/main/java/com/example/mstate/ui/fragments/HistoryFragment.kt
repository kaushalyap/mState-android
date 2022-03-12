package com.example.mstate.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mstate.adapters.HistoryAdapter
import com.example.mstate.databinding.FragmentHistoryBinding
import com.example.mstate.models.HistoryItem
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.HistoryCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    internal val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: HistoryAdapter
    private lateinit var firestoreService: FirestoreService
    private lateinit var auth: FirebaseAuth

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
        readTestHistory()
    }

    private fun readTestHistory() {
        firestoreService = FirestoreService()
        auth = Firebase.auth
        firestoreService.readHistory(object : HistoryCallback {
            override fun onPostExecute(histories: List<HistoryItem>?) {
                if (histories?.isNotEmpty() ?: return) {
                    adapter = HistoryAdapter(histories)
                    binding.recyclerView.adapter = adapter
                } else
                    binding.txtEmpty.visibility = View.VISIBLE
            }
        }, auth.currentUser?.uid.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}