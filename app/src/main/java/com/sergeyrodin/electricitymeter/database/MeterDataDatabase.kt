package com.sergeyrodin.electricitymeter.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=[MeterData::class], version = 1, exportSchema = false)
abstract class MeterDataDatabase: RoomDatabase() {
    abstract val meterDataDatabaseDao: MeterDataDatabaseDao
}