package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.utils.getMonthBoundariesByDate

class RoomMeterDataSource(private val meterDataDao: MeterDataDatabaseDao): MeterDataSource {

    override suspend fun insert(meterData: MeterData) {
        meterDataDao.insert(meterData)
    }

    override fun getMeterData(): LiveData<List<MeterData>> {
        return meterDataDao.getMeterData()
    }

    override fun getMonthMeterData(dateOfMonthToDisplay: Long): LiveData<List<MeterData>> {
        val boundaries = getMonthBoundariesByDate(dateOfMonthToDisplay)
        return meterDataDao.getMonthMeterData(boundaries.lastDayOfPrevMonthMillis, boundaries.lastDayOfCurrentMonthMillis)
    }
}