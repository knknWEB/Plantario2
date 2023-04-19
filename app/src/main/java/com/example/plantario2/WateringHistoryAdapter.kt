package com.example.plantario2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantario2.model.Watered

class WateringHistoryAdapter(private val wateringList: List<Watered>) : RecyclerView.Adapter<WateringHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wateringDate: TextView = view.findViewById(R.id.watered_date_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.watered_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val watering = wateringList[position]
        holder.wateringDate.text = watering.wateredDate.toString()
    }

    override fun getItemCount(): Int {
        return wateringList.size
    }
}
