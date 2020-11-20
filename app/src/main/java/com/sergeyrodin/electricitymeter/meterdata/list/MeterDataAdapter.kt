package com.sergeyrodin.electricitymeter.meterdata.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R

class MeterDataAdapter: RecyclerView.Adapter<MeterDataItemViewHolder>() {

    var data = listOf<String>()
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
        holder.textView.text = item
    }

    override fun getItemCount(): Int = data.size

}

class MeterDataItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val textView = itemView as TextView
}