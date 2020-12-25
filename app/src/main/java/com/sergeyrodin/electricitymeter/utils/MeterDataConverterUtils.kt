package com.sergeyrodin.electricitymeter.utils

import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation
import com.sergeyrodin.electricitymeter.meterdata.list.calculateDailyKwh
import com.sergeyrodin.electricitymeter.meterdata.list.calculateDailyPrice

fun convertMeterDataListToPresentationList(meterData: List<MeterData>) =
    if (meterData.isNotEmpty()) {
        var prevData = -1
        val firstData = meterData.first().data
        meterData.map { currentData ->
            val dailyKw = calculateDailyKwh(prevData, currentData)
            prevData = currentData.data
            val dailyPrice = calculateDailyPrice(dailyKw, currentData, firstData)
            MeterDataPresentation(currentData.data, currentData.date, dailyKw, dailyPrice)
        }
    } else {
        listOf()
    }