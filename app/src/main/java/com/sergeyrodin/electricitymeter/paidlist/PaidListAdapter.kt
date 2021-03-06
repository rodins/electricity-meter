package com.sergeyrodin.electricitymeter.paidlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.databinding.PaidDateItemBinding

class PaidListAdapter(
    private val clickListener: PaidDateClickListener,
    private val longClickListener: PaidDateLongClickListener
): RecyclerView.Adapter<PaidListAdapter.ViewHolder>() {

    var data = listOf<PricePaidDate>()
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
        val binding = PaidDateItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], clickListener, longClickListener, position, highlightedPosition)
    }

    override fun getItemCount() = data.size

    class ViewHolder(private val binding: PaidDateItemBinding): RecyclerView.ViewHolder(binding.root)  {

        fun bind(
            pricePaidDate: PricePaidDate,
            clickListener: PaidDateClickListener,
            longClickListener: PaidDateLongClickListener,
            position: Int,
            highlightedPosition: Int
        ) {
            binding.pricePaidDate = pricePaidDate
            binding.paidDateClickListener = clickListener
            binding.paidDateLayout.setOnLongClickListener {
                longClickListener.onLongClick(position)
                true
            }

            if(highlightedPosition == -1) {
                 binding.paidDateLayout.setBackgroundResource(R.color.design_default_color_background)
            }else {
                 binding.paidDateLayout.setBackgroundResource(R.color.design_default_color_secondary)
            }

            binding.executePendingBindings()
        }
    }
}

class PaidDateClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(id: Int) = clickListener(id)
}

class PaidDateLongClickListener(val clickListener: (position: Int) -> Unit) {
    fun onLongClick(position: Int) = clickListener(position)
}