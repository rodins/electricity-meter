package com.sergeyrodin.electricitymeter.meterdata.list

import com.sergeyrodin.electricitymeter.database.MeterData

fun calculateDiffKwh(
    prevData: Int,
    data: MeterData
): Int {
    return if (prevData != -1) data.data - prevData else 0
}

fun getTotalKwh(meterData: List<MeterData>): Int {
    val first = meterData.first()
    val last = meterData.last()
    return last.data - first.data
}

fun getAverageKwh(meterData: List<MeterData>): Int {
    val total = getTotalKwh(meterData)
    val numberOfItems = meterData.size - 1
    return total / numberOfItems
}