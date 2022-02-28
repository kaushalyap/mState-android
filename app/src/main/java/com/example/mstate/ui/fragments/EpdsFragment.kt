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
import com.example.mstate.models.EpdsDepressionLevels
import com.example.mstate.models.EpdsScoring
import com.example.mstate.models.QuestionItem
import com.example.mstate.models.QuestionnaireType

class EpdsFragment : Fragment() {

    private var _binding: FragmentEpdsBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: EpdsAdapter
    private var items: Array<QuestionItem> = emptyArray()

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

    private fun setupRecyclerView() {
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewEPDS.layoutManager = linearLayoutManager
        items = arrayOf(
            QuestionItem(
                resources.getString(R.string._1),
                resources.getString(R.string._1_i_have_been_able_to_laugh_and_see_the_funny_side_of_things),
                resources.getString(R.string.as_much_as_i_always_could),
                resources.getString(R.string.not_quite_so_much_now),
                resources.getString(R.string.definitely_not_so_much_now),
                resources.getString(R.string.not_at_all),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._2),
                resources.getString(R.string._2_feeling_down_depressed_or_hopeless),
                resources.getString(R.string.as_much_as_i_ever_did),
                resources.getString(R.string.rather_less_than_i_used_to),
                resources.getString(R.string.definitely_less_than_i_used_to),
                resources.getString(R.string.hardly_at_all),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._3),
                resources.getString(R.string._3_i_have_blamed_myself_unnecessarily_when_things_went_wrong),
                resources.getString(R.string.yes_most_of_the_time),
                resources.getString(R.string.yes_some_of_the_time),
                resources.getString(R.string.not_very_often),
                resources.getString(R.string.no_never),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._4),
                resources.getString(R.string._4_i_have_been_anxious_or_worried_for_no_good_reason),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.hardly_ever),
                resources.getString(R.string.yes_sometimes),
                resources.getString(R.string.yes_very_often),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._5),
                resources.getString(R.string._5_i_have_felt_scared_or_panicky_for_no_very_good_reason),
                resources.getString(R.string.yes_quite_a_lot),
                resources.getString(R.string.yes_sometimes),
                resources.getString(R.string.no_not_much),
                resources.getString(R.string.no_not_at_all),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._6),
                resources.getString(R.string._6_things_have_been_getting_on_top_of_me),
                resources.getString(R.string.yes_most_of_the_time_i_haven_t_been_able_to_cope_at_all),
                resources.getString(R.string.yes_sometimes_i_haven_t_been_coping_as_well_as_usual),
                resources.getString(R.string.no_most_of_the_time_i_have_coped_quite_well),
                resources.getString(R.string.no_i_have_been_coping_as_well_as_ever),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._7),
                resources.getString(R.string._7i_have_been_so_unhappy_that_i_have_had_difficulty_sleeping),
                resources.getString(R.string.yes_most_of_the_time),
                resources.getString(R.string.yes_sometimes),
                resources.getString(R.string.not_very_often),
                resources.getString(R.string.no_not_at_all),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._8),
                resources.getString(R.string._8_i_have_felt_sad_or_miserable),
                resources.getString(R.string.yes_most_of_the_time),
                resources.getString(R.string.yes_quite_often),
                resources.getString(R.string.not_very_often),
                resources.getString(R.string.no_not_at_all),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._9),
                resources.getString(R.string._9_i_have_been_so_unhappy_that_i_have_been_crying),
                resources.getString(R.string.yes_most_of_the_time),
                resources.getString(R.string.yes_quite_often),
                resources.getString(R.string.only_occasionally),
                resources.getString(R.string.no_never),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._10),
                resources.getString(R.string._10_the_thought_of_harming_myself_has_occurred_to_me),
                resources.getString(R.string.yes_quite_often),
                resources.getString(R.string.sometimes),
                resources.getString(R.string.hardly_ever),
                resources.getString(R.string.never),
                -1,
                resources.getString(R.string.please_select_a_choice)
            )
        )
        adapter = EpdsAdapter(items)
        binding.recyclerViewEPDS.adapter = adapter
    }

    private fun calculateScore(): String {
        return EpdsScoring(items).diagnosis()
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