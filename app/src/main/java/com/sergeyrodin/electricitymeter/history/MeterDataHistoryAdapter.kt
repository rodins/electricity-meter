package com.sergeyrodin.electricitymeter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataItemViewHolder
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation

class MeterDataHistoryAdapter: RecyclerView.Adapter<MeterDataItemViewHolder>() {

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