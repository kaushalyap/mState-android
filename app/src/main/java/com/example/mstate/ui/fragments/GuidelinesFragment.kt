package com.example.mstate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.databinding.FragmentGuidelinesBinding
import com.example.mstate.models.AutomatedResponse
import com.example.mstate.models.Guider
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.QuestionnaireType
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.HistoryCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GuidelinesFragment : Fragment() {

    private var _binding: FragmentGuidelinesBinding? = null
    internal val binding get() = _binding!!
    private lateinit var firestoreService: FirestoreService
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidelinesBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setGuidelines()
        binding.btn.setOnClickListener {
            findNavController().popBackStack()
            respondAutomatically()
        }
    }

    private fun respondAutomatically() {
        val automatedResponse = AutomatedResponse()
        automatedResponse.respond(requireActivity(), requireContext())
    }

    private fun setGuidelines() {
        auth = Firebase.auth
        firestoreService = FirestoreService()
        firestoreService.readLastThreeHistories(object : HistoryCallback {
            override fun onPostExecute(histories: List<HistoryItem>?) {
                if (histories != null) {
                    val lastTests = getHistoriesRelatedToLastItem(histories)
                    val guidelines = Guider(lastTests).generateGuidelines()
                    binding.txtGuidelines.text = guidelines
                }
            }
        }, auth.currentUser?.uid.toString())
    }

    fun getHistoriesRelatedToLastItem(histories: List<HistoryItem>): List<HistoryItem> {
        val lastQuestionnaireType = histories[0].questionnaireType
        val lastTests = mutableListOf<HistoryItem>()

        if (lastQuestionnaireType == QuestionnaireType.PHQ9.name) {
            for (item in histories)
                if (item.questionnaireType == QuestionnaireType.PHQ9.name)
                    lastTests.add(item)
        } else {
            for (item in histories)
                if (item.questionnaireType == QuestionnaireType.PHQ9.name)
                    lastTests.add(item)
        }
        return lastTests
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}