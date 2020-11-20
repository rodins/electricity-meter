package com.sergeyrodin.electricitymeter.database

data class MeterData(
        var data: Int,
        var date: Long = System.currentTimeMillis()
)