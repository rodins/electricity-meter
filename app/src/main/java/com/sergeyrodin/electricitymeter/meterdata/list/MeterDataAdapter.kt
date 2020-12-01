package com.sergeyrodin.electricitymeter.meterdata.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.dateToString

class MeterDataAdapter: RecyclerView.Adapter<MeterDataItemViewHolder>() {

    var data = listOf<MeterDataPresentation>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeterDataItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.meter_data_list_item, parent, false)
        return MeterDataItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeterDataItemViewHolder, position: Int) {
        val item = data[position]
        holder.dateText.text = dateToString(item.date)
        holder.dataText.text = holder.itemView.context.getString(R.string.kwh_format, item.data)
        holder.dailyKwhText.text = holder.itemView.context
            .getString(R.string.kwh_format, item.diff)
        holder.dailyPriceText.text = holder.itemView.context
            .getString(R.string.price_format, item.price)
    }

    override fun getItemCount(): Int = data.size
}

class MeterDataItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val dateText: TextView = itemView.findViewById(R.id.date_text)
    val dataText: TextView = itemView.findViewById(R.id.data_text)
    val dailyKwhText: TextView = itemView.findViewById(R.id.daily_kwh_text)
    val dailyPriceText: TextView = itemView.findViewById(R.id.daily_price_text)
}