package com.sergeyrodin.electricitymeter.meterdata.list

data class MeterDataPresentation(
    val id: Int,
    val data: Int,
    val date: Long,
    val diff: Int,
    val price: Double
)