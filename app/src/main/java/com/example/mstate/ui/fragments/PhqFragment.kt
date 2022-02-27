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
import com.example.mstate.models.QuestionItem

class PhqFragment : Fragment() {

    private var _binding: FragmentPhqBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: Phd9Adapter
    private var items: Array<QuestionItem> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhqBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewPhq9.layoutManager = linearLayoutManager
        items = arrayOf(
            QuestionItem(
                resources.getString(R.string._1),
                resources.getString(R.string._1_little_interest_or_pleasure_in_doing_things),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._2),
                resources.getString(R.string._2_feeling_down_depressed_or_hopeless),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._3),
                resources.getString(R.string._3_trouble_falling_or_staying_asleep_or_sleeping_too_much),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._4),
                resources.getString(R.string._4_feeling_tired_or_having_little_energy),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._5),
                resources.getString(R.string._5_poor_appetite_or_overeating),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._6),
                resources.getString(R.string._6_feeling_bad_about_yourself_or_that_you_are_a_failure_or_have_let_yourself_or_your_family_down),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._7),
                resources.getString(R.string._7_trouble_concentrating_on_things_such_as_reading_the_newspaper_or_watching_television),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._8),
                resources.getString(R.string._8_moving_or_speaking_so_slowly_that_other_people_could_have_noticed_or_the_opposite_being_so_figety_or_restless_that_you_have_been_moving_around_a_lot_more_than_usual),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
            QuestionItem(
                resources.getString(R.string._9),
                resources.getString(R.string._9_thoughts_that_you_would_be_better_off_dead_or_of_hurting_yourself),
                resources.getString(R.string.not_at_all),
                resources.getString(R.string.several_days),
                resources.getString(R.string.more_than_half_the_days),
                resources.getString(R.string.nearly_every_day),
                -1,
                resources.getString(R.string.please_select_a_choice)
            ),
        )

        adapter = Phd9Adapter(items)
        binding.recyclerViewPhq9.adapter = adapter
        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                findNavController().navigate(R.id.action_phq_to_result)
            }
        }
    }

    private fun calculateScore() {
        TODO("Not yet implemented")
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