package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.utils.getMonthBoundariesByDate
import com.sergeyrodin.electricitymeter.utils.wrapEspressoIdlingResource

class RoomMeterDataSource(private val meterDataDao: MeterDataDatabaseDao): MeterDataSource {

    override suspend fun insert(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.insert(meterData)
        }
    }

    override fun getMeterData(): LiveData<List<MeterData>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getMeterData()
        }
    }

    override fun getMonthMeterData(dateOfMonthToDisplay: Long): LiveData<List<MeterData>> {
        val boundaries = getMonthBoundariesByDate(dateOfMonthToDisplay)
        return meterDataDao.getMonthMeterData(boundaries.lastDayOfPrevMonthMillis, boundaries.lastDayOfCurrentMonthMillis)
    }

    override suspend fun getMeterDataBetweenDates(beginDate: Long, endDate: Long): List<MeterData>? {
        wrapEspressoIdlingResource {
            return meterDataDao.getMeterDataBetweenDates(beginDate, endDate)
        }
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        wrapEspressoIdlingResource {
            meterDataDao.insertPaidDate(paidDate)
        }
    }

    override suspend fun getLastPaidDate(): PaidDate? {
        wrapEspressoIdlingResource {
            return meterDataDao.getLastPaidDate()
        }
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        wrapEspressoIdlingResource {
            meterDataDao.deletePaidDate(paidDate)
        }
    }

    override fun getPaidDates(): LiveData<List<PaidDate>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getPaidDates()
        }
    }

    override fun getPaidDatesRangeById(id: Int): List<PaidDate>? {
        wrapEspressoIdlingResource {
            return meterDataDao.getPaidDatesRangeById(id)
        }
    }
}