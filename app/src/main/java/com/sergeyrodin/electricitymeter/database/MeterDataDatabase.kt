package com.sergeyrodin.electricitymeter.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[MeterData::class, PaidDate::class], version = 2, exportSchema = false)
abstract class MeterDataDatabase: RoomDatabase() {
    abstract val meterDataDatabaseDao: MeterDataDatabaseDao
}