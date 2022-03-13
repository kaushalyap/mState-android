package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mstate.R
import com.example.mstate.adapters.EpdsAdapter
import com.example.mstate.databinding.FragmentEpdsBinding
import com.example.mstate.models.*
import com.example.mstate.services.FirestoreService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EpdsFragment : Fragment() {

    private var _binding: FragmentEpdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: EpdsAdapter
    private var items: Array<QuestionItem> = emptyArray()
    private lateinit var firestoreService: FirestoreService
    private lateinit var epdsScoring: EpdsScoring
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpdsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        setupRecyclerView()

        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                val diagnosis = calculateScore()
                saveTestResult()
                if (diagnosis == EpdsDepressionLevels.Undefined.diagnosis || diagnosis == EpdsDepressionLevels.Not.diagnosis)
                    findNavController().navigate(R.id.action_epds_to_normal)
                else {
                    val action = EpdsFragmentDirections.actionEpdsToDepressed(
                        diagnosis,
                        QuestionnaireType.EPDS.name
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun saveTestResult() {
        val historyItem = HistoryItem(
            Timestamp.now(),
            QuestionnaireType.EPDS.name,
            epdsScoring.getScore()
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
        binding.recyclerViewEPDS.layoutManager = linearLayoutManager
        items = QuestionLibrary().getEpdsQuestions(requireContext())
        adapter = EpdsAdapter(items)
        binding.recyclerViewEPDS.adapter = adapter
    }

    private fun calculateScore(): String {
        epdsScoring = EpdsScoring(items)
        return epdsScoring.diagnosis()
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