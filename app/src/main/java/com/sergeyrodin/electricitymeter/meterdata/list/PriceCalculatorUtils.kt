package com.sergeyrodin.electricitymeter.meterdata.list

import com.sergeyrodin.electricitymeter.database.MeterData

private const val PRICE_KWH_SMALL = 0.9
private const val PRICE_KWH_BIG = 1.68
private const val SMALL_PRICE_KW = 100
private const val PRICE_100_KWH = PRICE_KWH_SMALL * SMALL_PRICE_KW

fun calculateDailyPrice(
    dailyKw: Int,
    currentData: MeterData,
    firstData: Int
): Double {
    var price = 0.0
    if (dailyKw > 0) {
        val currentTotalKw = currentData.data - firstData
        if (currentTotalKw > SMALL_PRICE_KW) {
            if (currentTotalKw - dailyKw > SMALL_PRICE_KW) {
                price = calculateBigPrice(dailyKw)
            } else {
                price = calculateMixedPrice(currentTotalKw, dailyKw)
            }
        } else {
            price = calculateSmallPrice(dailyKw)
        }
    }
    return price
}

private fun calculateBigPrice(dailyKw: Int) = dailyKw * PRICE_KWH_BIG

private fun calculateMixedPrice(
    currentTotalKw: Int,
    dailyKw: Int
): Double {
    val bigPriceKw = currentTotalKw - SMALL_PRICE_KW
    val smallPriceKw = dailyKw - bigPriceKw
    val smallPrice = calculateSmallPrice(smallPriceKw)
    val bigPrice = calculateBigPrice(bigPriceKw)
    return smallPrice + bigPrice
}

private fun calculateSmallPrice(dailyKw: Int) = dailyKw * PRICE_KWH_SMALL

fun calculateTotalPrice(totalKwh: Int): Double {
    if (totalKwh > 100) {
        return (totalKwh - 100) * PRICE_KWH_BIG + PRICE_100_KWH
    } else {
        return calculateSmallPrice(totalKwh)
    }
}