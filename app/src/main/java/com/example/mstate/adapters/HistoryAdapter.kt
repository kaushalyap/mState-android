package com.example.mstate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mstate.R
import com.example.mstate.models.HistoryItem

class HistoryAdapter(private val dataSet: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lbDate: TextView
        val lbTime: TextView
        val lbType: TextView
        val lbScore: TextView

        init {
            lbDate = view.findViewById(R.id.lbDate)
            lbTime = view.findViewById(R.id.lbTime)
            lbType = view.findViewById(R.id.lbQuestionnaireType)
            lbScore = view.findViewById(R.id.lbScore)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_history, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.lbDate.text = dataSet[position].date
        viewHolder.lbTime.text = dataSet[position].time
        viewHolder.lbType.text = dataSet[position].questionnaireType
        viewHolder.lbScore.text = dataSet[position].score.toString()
    }

    override fun getItemCount(): Int = dataSet.size

}
