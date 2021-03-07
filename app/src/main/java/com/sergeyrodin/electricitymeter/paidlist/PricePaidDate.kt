package com.sergeyrodin.electricitymeter.paidlist

data class PricePaidDate(
    val id: Int,
    val date: Long,
    val price: Double,
    var isHighlighted: Boolean = false
)