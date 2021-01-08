package com.sergeyrodin.electricitymeter.meterdata.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.history.MeterDataHistoryAdapter

class MeterDataAdapter(private val clickListener: MeterDataClickListener): MeterDataHistoryAdapter() {

    override fun onBindViewHolder(holder: MeterDataItemViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            clickListener.onClick(getItem(position).id)
        }
    }
}

class MeterDataItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val dateText: TextView = itemView.findViewById(R.id.date_text)
    val dataText: TextView = itemView.findViewById(R.id.data_text)
    val dailyKwhText: TextView = itemView.findViewById(R.id.daily_kwh_text)
    val dailyPriceText: TextView = itemView.findViewById(R.id.daily_price_text)
}

class MeterDataClickListener(private val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}