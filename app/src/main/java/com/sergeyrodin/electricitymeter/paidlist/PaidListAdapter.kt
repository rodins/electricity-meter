package com.sergeyrodin.electricitymeter.paidlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.dateToString

class PaidListAdapter(private val clickListener: PaidDateClickListener): RecyclerView.Adapter<PaidListAdapter.ViewHolder>() {

    var data = listOf<PaidDate>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.paid_date_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = dateToString(item.date)
        holder.textView.setOnClickListener {
            clickListener.onClick(item.id)
        }
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
        val textView = itemView as TextView
    }
}

class PaidDateClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}