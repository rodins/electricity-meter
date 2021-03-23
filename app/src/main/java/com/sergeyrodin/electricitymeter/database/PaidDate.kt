package com.sergeyrodin.electricitymeter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "paid_dates",
    foreignKeys = [ForeignKey(
        entity = Price::class,
        parentColumns = ["id"],
        childColumns = ["price_id"],
        onDelete = ForeignKey.SET_DEFAULT
    )])
data class PaidDate(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Long,
    @ColumnInfo(name = "price_id", index = true, defaultValue = "0")
    val priceId: Int = 0
)