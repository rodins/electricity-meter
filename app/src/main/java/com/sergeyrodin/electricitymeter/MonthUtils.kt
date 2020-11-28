package com.sergeyrodin.electricitymeter

import java.util.*

data class MonthBoundaries(
    val lastDayOfPrevMonthMillis: Long,
    val lastDayOfCurrentMonthMillis: Long
)

fun getMonthBoundariesByDate(dateMillis: Long): MonthBoundaries {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val lastDayOfPrevMonthMillis = calendar.timeInMillis
    calendar.add(Calendar.MONTH, 1)
    val lastDayOfCurrentMonthMillis = calendar.timeInMillis

    return MonthBoundaries(lastDayOfPrevMonthMillis, lastDayOfCurrentMonthMillis)
}