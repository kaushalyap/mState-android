package com.example.mstate.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mstate.R
import com.example.mstate.models.QuestionItem

class Phd9Adapter(var dataSet: Array<QuestionItem>) :
    RecyclerView.Adapter<Phd9Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lbQuestionNo: TextView
        val lbQuestion: TextView
        val rb1: RadioButton
        val rb2: RadioButton
        val rb3: RadioButton
        val rb4: RadioButton
        val radioGroup: RadioGroup
        val lbError: TextView

        init {
            lbQuestionNo = view.findViewById(R.id.lbQuestionNo)
            lbQuestion = view.findViewById(R.id.lbQuestion)
            rb1 = view.findViewById(R.id.radioAnswer1)
            rb2 = view.findViewById(R.id.radioAnswer2)
            rb3 = view.findViewById(R.id.radioAnswer3)
            rb4 = view.findViewById(R.id.radioAnswer4)
            radioGroup = view.findViewById(R.id.radioGroup)
            lbError = view.findViewById(R.id.lbError)
        }
    }

    // Restoring saved state when scrolling
    private fun setRadio(holder: ViewHolder, selection: Int) {
        Log.d(TAG, "Selection : $selection")

        val b1: RadioButton = holder.rb1
        val b2: RadioButton = holder.rb2
        val b3: RadioButton = holder.rb3
        val b4: RadioButton = holder.rb4

        b1.isChecked = selection == 0
        b2.isChecked = selection == 1
        b3.isChecked = selection == 2
        b4.isChecked = selection == 3
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_question, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.lbQuestionNo.text = dataSet[position].questionNo
        viewHolder.lbQuestion.text = dataSet[position].question
        viewHolder.rb1.text = dataSet[position].answer1
        viewHolder.rb2.text = dataSet[position].answer2
        viewHolder.rb3.text = dataSet[position].answer3
        viewHolder.rb4.text = dataSet[position].answer4


        setRadio(viewHolder, dataSet[position].selected)

        // Update dataset according to radio button click
        viewHolder.rb1.setOnClickListener {
            dataSet[position].selected = 0
            setRadio(viewHolder, dataSet[position].selected)
        }
        viewHolder.rb2.setOnClickListener {
            dataSet[position].selected = 1
            setRadio(viewHolder, dataSet[position].selected)
        }
        viewHolder.rb3.setOnClickListener {
            dataSet[position].selected = 2
            setRadio(viewHolder, dataSet[position].selected)
        }
        viewHolder.rb4.setOnClickListener {
            dataSet[position].selected = 3
            setRadio(viewHolder, dataSet[position].selected)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    companion object {
        const val TAG: String = "Phd9Adapter"
    }
}