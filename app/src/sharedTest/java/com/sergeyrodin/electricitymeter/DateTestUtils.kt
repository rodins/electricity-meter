package com.sergeyrodin.electricitymeter.utils

import java.util.*

fun dateToLong(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day, hour, minute)
    return calendar.timeInMillis
}