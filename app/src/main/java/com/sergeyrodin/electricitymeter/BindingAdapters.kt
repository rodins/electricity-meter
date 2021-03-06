package com.sergeyrodin.electricitymeter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sergeyrodin.electricitymeter.meterdata.dateToString

@BindingAdapter("date")
fun dateToTextView(textView: TextView, date: Long?) {
    date?.let {
        textView.text = dateToString(date)
    }
}

@BindingAdapter("price")
fun priceToTextView(textView: TextView, price: Double?) {
    textView.text = price?.toString()
}