package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.utils.wrapEspressoIdlingResource

class RoomMeterDataSource(private val meterDataDao: MeterDataDatabaseDao): MeterDataSource {

    override suspend fun insert(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.insert(meterData)
        }
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

    override suspend fun getPaidDatesRangeById(id: Int): List<PaidDate>? {
        wrapEspressoIdlingResource {
            return meterDataDao.getPaidDatesRangeById(id)
        }
    }

    override suspend fun deleteAllMeterData() {
        wrapEspressoIdlingResource {
            meterDataDao.deleteAllMeterData()
        }
    }

    override suspend fun deleteAllPaidDates() {
        wrapEspressoIdlingResource {
            meterDataDao.deleteAllPaidDates()
        }
    }

    override suspend fun getMeterDataById(id: Int): MeterData? {
        wrapEspressoIdlingResource {
            return meterDataDao.getMeterDataById(id)
        }
    }

    override suspend fun update(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.update(meterData)
        }
    }
}