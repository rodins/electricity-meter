package com.sergeyrodin.electricitymeter.prices.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.Price

class PriceListAdapter(
    private val longClickListener: PriceLongClickListener,
    private val clickListener: PriceClickListener
) : RecyclerView.Adapter<PriceListAdapter.ViewHolder>() {

    var prices = listOf<Price>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedPrice: Price? = null
        set(value) {
            if(value == null) {
                val prevValue = field
                field = value
                prevValue?.let{
                    notifyItemChanged(prices.indexOf(it))
                }
            } else {
                field = value
                notifyItemChanged(prices.indexOf(value))
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.price_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val price = prices[position]
        holder.bind(price, selectedPrice, longClickListener, clickListener)
    }

    override fun getItemCount(): Int {
        return prices.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            price: Price,
            selectedPrice: Price?,
            longClickListener: PriceLongClickListener,
            clickListener: PriceClickListener
        ) {
            val textView = view as TextView
            textView.text = price.price.toString()

            textView.setOnLongClickListener {
                longClickListener.onLongClick(price)
                true
            }

            textView.setOnClickListener {
                clickListener.onClick()
            }

            val resource = getBackgroundResource(selectedPrice, price)
            textView.setBackgroundResource(resource)
        }

        private fun getBackgroundResource(
            selectedPrice: Price?,
            price: Price
        ): Int {
            return if (selectedPrice == price) {
                R.color.design_default_color_secondary
            } else {
                R.color.design_default_color_background
            }
        }

    }

}

class PriceLongClickListener(private val listener: (price: Price) -> Unit) {
    fun onLongClick(price: Price) = listener(price)
}

class PriceClickListener(private val listener: () -> Unit) {
    fun onClick() = listener()
}