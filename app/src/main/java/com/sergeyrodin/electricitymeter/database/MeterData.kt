package com.sergeyrodin.electricitymeter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meter_data")
data class MeterData(
    var data: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Long = System.currentTimeMillis()
)