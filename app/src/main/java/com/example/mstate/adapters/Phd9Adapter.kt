package com.example.mstate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mstate.R
import com.example.mstate.models.QuestionItem

class Phd9Adapter(private val dataSet: Array<QuestionItem>) :
    RecyclerView.Adapter<Phd9Adapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lbQuestionNo: TextView
        val lbQuestion: TextView
        val lbAnswer1: TextView
        val lbAnswer2: TextView
        val lbAnswer3: TextView
        val lbAnswer4: TextView
        val lbError: TextView

        init {
            lbQuestionNo = view.findViewById(R.id.lbQuestionNo)
            lbQuestion = view.findViewById(R.id.lbQuestion)
            lbAnswer1 = view.findViewById(R.id.radioAnswer1)
            lbAnswer2 = view.findViewById(R.id.radioAnswer2)
            lbAnswer3 = view.findViewById(R.id.radioAnswer3)
            lbAnswer4 = view.findViewById(R.id.radioAnswer4)
            lbError = view.findViewById(R.id.lbError)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_question, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.lbQuestionNo.text = dataSet[position].questionNo
        viewHolder.lbQuestion.text = dataSet[position].question
        viewHolder.lbAnswer1.text = dataSet[position].answer1
        viewHolder.lbAnswer2.text = dataSet[position].answer2
        viewHolder.lbAnswer3.text = dataSet[position].answer3
        viewHolder.lbAnswer4.text = dataSet[position].answer4
    }

    override fun getItemCount(): Int = dataSet.size

}