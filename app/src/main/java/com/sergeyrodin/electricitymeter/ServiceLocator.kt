package com.sergeyrodin.electricitymeter

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.datasource.RoomMeterDataSource

object ServiceLocator {
    @Volatile
    private var database: MeterDataDatabase? = null

    @Volatile
    var dataSource: MeterDataSource? = null
        @VisibleForTesting set

    private val lock = Any()

    fun provideMeterDataSource(context: Context): MeterDataSource {
        synchronized(this) {
            return dataSource ?: createMeterDataSource(context)
        }
    }

    private fun createMeterDataSource(context: Context): MeterDataSource {
        val newDataSource = RoomMeterDataSource(createMeterDataDatabaseDao(context))
        dataSource = newDataSource
        return newDataSource
    }

    private fun createMeterDataDatabaseDao(context: Context): MeterDataDatabaseDao {
        val meterDataDatabase = database ?: createDatabase(context)
        return meterDataDatabase.meterDataDatabaseDao
    }

    private fun createDatabase(context: Context): MeterDataDatabase {
        val meterDataDatabase = Room.databaseBuilder(
            context,
            MeterDataDatabase::class.java,
            "meter_data_database"
        )
            .fallbackToDestructiveMigration()
            .build()
        database = meterDataDatabase
        return meterDataDatabase
    }

    @VisibleForTesting
    fun resetDataSource() {
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            dataSource = null
        }
    }
}