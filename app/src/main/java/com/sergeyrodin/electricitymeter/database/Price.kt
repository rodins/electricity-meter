package com.sergeyrodin.electricitymeter.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prices")
data class Price(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var price: Double
)