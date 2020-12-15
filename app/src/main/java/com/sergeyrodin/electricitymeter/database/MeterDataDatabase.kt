package com.sergeyrodin.electricitymeter.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[MeterData::class, PaidDate::class], version = 2, exportSchema = false)
abstract class MeterDataDatabase: RoomDatabase() {
    abstract val meterDataDatabaseDao: MeterDataDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: MeterDataDatabase? = null
        fun getInstance(context: Context): MeterDataDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context,
                        MeterDataDatabase::class.java,
                        "meter_data_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        @VisibleForTesting
        fun reset() {
            synchronized(this) {
                INSTANCE?.apply{
                    clearAllTables()
                    close()
                }
                INSTANCE = null
            }
        }
    }
}