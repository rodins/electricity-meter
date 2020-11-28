package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData

interface MeterDataSource {

    suspend fun insert(meterData: MeterData)

    fun getMeterData(): LiveData<List<MeterData>>
    fun getMonthMeterData(dateOfMonthToDisplay: Long): LiveData<List<MeterData>>
}