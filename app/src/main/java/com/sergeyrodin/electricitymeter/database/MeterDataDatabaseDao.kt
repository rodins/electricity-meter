package com.sergeyrodin.electricitymeter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeterDataDatabaseDao {

    @Insert
    suspend fun insert(meterData: MeterData)

    @Query("SELECT * FROM meter_data")
    fun getMeterData(): LiveData<List<MeterData>>

    @Query("SELECT * FROM meter_data WHERE date >= :prevMonthMillis AND date <= :currentMonthMillis")
    fun getMonthMeterData(prevMonthMillis: Long, currentMonthMillis: Long): LiveData<List<MeterData>>

}