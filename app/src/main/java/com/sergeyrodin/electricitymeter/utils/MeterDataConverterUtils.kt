package com.sergeyrodin.electricitymeter.utils

import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation

fun convertMeterDataListToPresentationList(meterData: List<MeterData>) =
    if (meterData.isNotEmpty()) {
        var prevData: MeterData? = null
        meterData.map { currentData ->
            val dailyKw = prevData?.let { calculateDailyKwByDates(it, currentData) } ?: 0
            prevData = currentData
            val dailyPrice = calculateDailyPrice(dailyKw)
            MeterDataPresentation(
                currentData.id,
                currentData.data,
                currentData.date,
                dailyKw,
                dailyPrice)
        }
    } else {
        listOf()
    }

fun convertMeterDataListToPresentationListReversed(meterData: List<MeterData>): List<MeterDataPresentation> {
    return convertMeterDataListToPresentationList(meterData).reversed()
}