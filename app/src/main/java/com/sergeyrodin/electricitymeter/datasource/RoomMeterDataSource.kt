package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao

class RoomMeterDataSource(private val meterDataDao: MeterDataDatabaseDao): MeterDataSource {
    override suspend fun insert(meterData: MeterData) {
        meterDataDao.insert(meterData)
    }

    override fun getMeterData(): LiveData<List<MeterData>> {
        return meterDataDao.getMeterData()
    }
}