package com.example.mstate.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mstate.R
import com.example.mstate.databinding.FragmentGuidelinesBinding
import com.example.mstate.models.AutomatedResponse
import com.example.mstate.models.Guider
import com.example.mstate.models.HistoryItem
import com.example.mstate.models.QuestionnaireType
import com.example.mstate.services.FirestoreService
import com.example.mstate.services.HistoryCallback

class GuidelinesFragment : Fragment() {

    private var _binding: FragmentGuidelinesBinding? = null
    internal val binding get() = _binding!!
    private lateinit var firestoreService: FirestoreService

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
            setGuidelines()
            respondAutomatically()
        }
    }

    private fun respondAutomatically() {
        val automatedResponse = AutomatedResponse()
        automatedResponse.respond(requireActivity(), requireContext())
    }

    private fun setGuidelines() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val docRef = sharedPref?.getString(getString(R.string.pref_user_doc_ref), null)
        firestoreService = FirestoreService()
        if (docRef != null) {
            firestoreService.readLastThreeHistories(object : HistoryCallback {
                override fun onCallback(histories: List<HistoryItem>?) {
                    val lastTests = getHistoriesRelatedToLastItem(histories ?: return)
                    val guidelines = Guider(lastTests).generateGuidelines()
                    binding.txtGuidelines.text = guidelines
                }
            }, docRef)
        }
    }

    fun getHistoriesRelatedToLastItem(histories: List<HistoryItem>): List<HistoryItem> {
        val lastQuestionnaireType = histories[0].questionnaireType
        val lastTests = mutableListOf<HistoryItem>()

        if (lastQuestionnaireType == QuestionnaireType.PHQ.name) {
            for (item in histories)
                if (item.questionnaireType == QuestionnaireType.PHQ.name)
                    lastTests.add(item)
        } else {
            for (item in histories)
                if (item.questionnaireType == QuestionnaireType.PHQ.name)
                    lastTests.add(item)
        }
        return lastTests
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}