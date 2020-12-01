package com.sergeyrodin.electricitymeter.meterdata

import java.text.DateFormat
import java.util.*

fun dateToString(date: Long): String {
    return DateFormat.getDateInstance(DateFormat.SHORT).format(Date(date))
}