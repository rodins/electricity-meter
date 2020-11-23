package com.sergeyrodin.electricitymeter.meterdata.list

data class MeterDataPresentation(
    val data: Int,
    val date: Long,
    val diff: Int
)