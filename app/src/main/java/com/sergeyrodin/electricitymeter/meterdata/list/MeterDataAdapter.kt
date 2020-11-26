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
        holder.dataText.text = item.data.toString()
        holder.diffText.text = item.diff.toString()
        holder.priceText.text = item.price.toString()
    }

    override fun getItemCount(): Int = data.size
}

class MeterDataItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val dateText: TextView = itemView.findViewById(R.id.date_text)
    val dataText: TextView = itemView.findViewById(R.id.data_item_text)
    val diffText: TextView = itemView.findViewById(R.id.daily_kw_text)
    val priceText: TextView = itemView.findViewById(R.id.daily_price_text)
}