package com.example.mstate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mstate.R
import com.example.mstate.models.HistoryItem

class HistoryAdapter(private val dataSet: Array<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lbDate: TextView
        val lbTime: TextView
        val lbType: TextView
        val lbDiagnosis: TextView

        init {
            lbDate = view.findViewById(R.id.lbDate)
            lbTime = view.findViewById(R.id.lbTime)
            lbType = view.findViewById(R.id.lbQuestionnaireType)
            lbDiagnosis = view.findViewById(R.id.lbDiagnosis)
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
        viewHolder.lbDiagnosis.text = dataSet[position].diagnosis
    }

    override fun getItemCount(): Int = dataSet.size

}
