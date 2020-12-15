package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.database.PaidDate
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

    override suspend fun getMeterDataBetweenDates(beginDate: Long, endDate: Long): List<MeterData>? {
        return meterDataDao.getMeterDataBetweenDates(beginDate, endDate)
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        meterDataDao.insertPaidDate(paidDate)
    }

    override suspend fun getPaidDate(): PaidDate? {
        return meterDataDao.getPaidDate()
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        meterDataDao.deletePaidDate(paidDate)
    }
}