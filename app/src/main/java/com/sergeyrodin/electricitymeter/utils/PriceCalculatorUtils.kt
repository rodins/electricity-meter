package com.sergeyrodin.electricitymeter.utils

import com.sergeyrodin.electricitymeter.database.MeterData
import java.util.concurrent.TimeUnit

private const val HOURS_IN_DAY = 24
private const val PROGNOSIS_DAYS_IN_MONTH = 30

fun calculateDailyPrice(
    dailyKw: Int,
    priceKwh: Double
): Double {
    var price = 0.0
    if (dailyKw > 0) {
        price = calculatePrice(dailyKw, priceKwh)
    }
    return price
}

fun calculatePrice(kwh: Int, price: Double) = (price.toBigDecimal() * kwh.toBigDecimal()).toDouble()

fun calculateTotalPrice(totalKwh: Int, priceKwh: Double): Double {
    return calculatePrice(totalKwh, priceKwh)
}

fun calculatePrognosisByDates(meterData: List<MeterData>, priceKwh: Double): Double {
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
        return calculatePrice(prognosisKwPerMonth.toInt(), priceKwh)
    }
    return 0.0
}

private fun calculatePrognosisKwPerMonth(kwPerHour: Double): Double {
    return calculateKwPerDay(kwPerHour) * PROGNOSIS_DAYS_IN_MONTH
}

private fun calculateKwPerDay(kwPerHour: Double): Double {
    return kwPerHour * HOURS_IN_DAY
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

fun calculateAvgByDates(meterData: List<MeterData>): Int {
    if(meterData.isNotEmpty()) {
        val meterData1 = meterData.first()
        val meterData2 = meterData.last()

        val diffData = calculateDiffData(meterData2, meterData1)
        val diffHours = calculateDiffHours(meterData2, meterData1)
        if(diffHours == 0L) {
            return calculateAvgNoDates(meterData, diffData)
        }

        return calculateDailyKw(diffData, diffHours)
    }
    return 0
}

private fun calculateAvgNoDates(
    meterData: List<MeterData>,
    diffData: Int
): Int {
    return if (meterData.size == 2) {
        diffData
    } else {
        diffData / meterData.size
    }
}

fun calculateDailyKwByDates(meterData1: MeterData, meterData2: MeterData) : Int {
    val diffData = calculateDiffData(meterData2, meterData1)
    val diffHours = calculateDiffHours(meterData2, meterData1)
    if(diffHours == 0L) {
        return diffData
    }

    return calculateDailyKw(diffData, diffHours)
}

private fun calculateDailyKw(diffData: Int, diffHours: Long): Int {
    val kwPerHour = calculateKwPerHour(diffData, diffHours)
    val kwPerDay = calculateKwPerDay(kwPerHour)
    return kwPerDay.toInt()
}
