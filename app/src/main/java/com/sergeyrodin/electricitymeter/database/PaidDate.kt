package com.sergeyrodin.electricitymeter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paid_dates")
data class PaidDate(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Long
)