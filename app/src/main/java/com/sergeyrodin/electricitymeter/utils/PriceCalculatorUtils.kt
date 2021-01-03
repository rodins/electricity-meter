package com.sergeyrodin.electricitymeter.utils

private const val PRICE_KWH = 1.68

fun calculateDailyPrice(
    dailyKw: Int,
): Double {
    var price = 0.0
    if (dailyKw > 0) {
        price = calculatePrice(dailyKw)
    }
    return price
}

private fun calculatePrice(kwh: Int) = (PRICE_KWH.toBigDecimal() * kwh.toBigDecimal()).toDouble()

fun calculateTotalPrice(totalKwh: Int): Double {
    return calculatePrice(totalKwh)
}