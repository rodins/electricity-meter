package com.sergeyrodin.electricitymeter.utils

import com.sergeyrodin.electricitymeter.database.MeterData
import java.util.concurrent.TimeUnit

private const val PRICE_KWH = 1.68
private const val HOURS_IN_DAY = 24
private const val PROGNOSIS_DAYS_IN_MONTH = 30

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

fun calculatePrognosisByDates(meterData: List<MeterData>): Double {
    if(meterData.isNotEmpty()) {
        val meterData1 = meterData.first()
        val meterData2 = meterData.last()

        val diffData = calculateDiffData(meterData2, meterData1)
        val diffHours = calculateDiffHours(meterData2, meterData1)
        if(diffHours == 0L) {
            return 0.0
        }
        val kwPerHour = calculateKwPerHour(diffData, diffHours)
        val prognosisKwPerMonth = calculatePrognosisKwPerMonth(kwPerHour)
        return calculatePrice(prognosisKwPerMonth.toInt())
    }
    return 0.0
}

private fun calculatePrognosisKwPerMonth(kwPerHour: Double): Double {
    return kwPerHour * HOURS_IN_DAY * PROGNOSIS_DAYS_IN_MONTH
}

private fun calculateKwPerHour(diffData: Int, diffHours: Long): Double {
    return diffData.toDouble() / diffHours
}

private fun calculateDiffHours(
    meterData2: MeterData,
    meterData1: MeterData
): Long {
    val diffDate = meterData2.date - meterData1.date
    return TimeUnit.MILLISECONDS.toHours(diffDate)
}

private fun calculateDiffData(
    meterData2: MeterData,
    meterData1: MeterData
): Int {
    return meterData2.data - meterData1.data
}