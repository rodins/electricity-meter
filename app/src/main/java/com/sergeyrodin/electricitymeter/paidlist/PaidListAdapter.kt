package com.sergeyrodin.electricitymeter.paidlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.dateToString

class PaidListAdapter(
    private val clickListener: PaidDateClickListener,
    private val longClickListener: PaidDateLongClickListener
): RecyclerView.Adapter<PaidListAdapter.ViewHolder>() {

    var data = listOf<PaidDate>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var highlightedPosition = -1
        set(value) {
            if(value != -1) {
                field = value
                notifyItemChanged(value)
            }else {
                val position = field
                field = value
                notifyItemChanged(position)
            }
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
        holder.textView.setOnLongClickListener {
            longClickListener.onLongClick(position)
            true
        }
        if(highlightedPosition == -1) {
            holder.textView.setBackgroundResource(R.color.design_default_color_background)
        }else {
            holder.textView.setBackgroundResource(R.color.design_default_color_secondary)
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

class PaidDateLongClickListener(val clickListener: (position: Int) -> Unit) {
    fun onLongClick(position: Int) = clickListener(position)
}