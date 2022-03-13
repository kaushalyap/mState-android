package com.example.mstate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mstate.R
import com.example.mstate.models.HistoryItem
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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

        val stamp = Timestamp(System.currentTimeMillis())
        val date = Date(stamp.time)
        println(date)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val sdf = SimpleDateFormat("dd MMM yyyy,hh:mm a", Locale.getDefault())
        val split = sdf.format(dataSet[position].timestamp?.toDate() ?: return).split(',')

        viewHolder.lbDate.text = split[0]
        viewHolder.lbTime.text = split[1]
        viewHolder.lbType.text = dataSet[position].questionnaireType
        viewHolder.lbScore.text = dataSet[position].score.toString()
    }

    override fun getItemCount(): Int = dataSet.size
}
