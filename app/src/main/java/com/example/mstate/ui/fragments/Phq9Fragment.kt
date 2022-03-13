package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mstate.R
import com.example.mstate.adapters.Phd9Adapter
import com.example.mstate.databinding.FragmentPhqBinding
import com.example.mstate.models.*
import com.example.mstate.services.FirestoreService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Phq9Fragment : Fragment() {

    private var _binding: FragmentPhqBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: Phd9Adapter
    private var items: Array<QuestionItem> = emptyArray()
    private lateinit var firestoreService: FirestoreService
    private lateinit var phq9Scoring: Phq9Scoring
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhqBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setupRecyclerView()

        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                val diagnosis = calculateScore()
                saveTestResult()
                if (diagnosis != Phq9DepressionLevels.Undefined.disorderName && diagnosis != Phq9DepressionLevels.Not.disorderName) {
                    val action = Phq9FragmentDirections.actionPhqToDepressed(
                        diagnosis,
                        QuestionnaireType.PHQ9.name
                    )
                    findNavController().navigate(action)
                } else
                    findNavController().navigate(R.id.action_phq_to_normal)
            }
        }
    }

    private fun saveTestResult() {
        val historyItem = HistoryItem(
            Timestamp.now(),
            QuestionnaireType.PHQ9.name,
            phq9Scoring.getScore()
        )
        firestoreService = FirestoreService()
        firebaseAuth = Firebase.auth

        if (firebaseAuth.currentUser?.uid != null) {
            val uid = firebaseAuth.currentUser?.uid.toString()
            firestoreService.addHistoryItem(uid, historyItem)
        } else {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.action_global_signIn)
        }

    }

    private fun setupRecyclerView() {
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewPhq9.layoutManager = linearLayoutManager
        items = QuestionLibrary().getPhq9Questions(requireContext())
        adapter = Phd9Adapter(items)
        binding.recyclerViewPhq9.adapter = adapter
    }

    private fun calculateScore(): String {
        phq9Scoring = Phq9Scoring(items)
        return phq9Scoring.diagnosis()
    }

    private fun isValid(): Boolean {
        val unmarkedItems = items.filter { it.selected < 0 }

        return if (unmarkedItems.isEmpty()) {
            binding.lbError.visibility = View.INVISIBLE
            true
        } else {
            binding.lbError.visibility = View.VISIBLE
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}