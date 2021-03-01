package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabaseDao
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.utils.wrapEspressoIdlingResource
import javax.inject.Inject

class RoomMeterDataSource @Inject constructor(
    private val meterDataDao: MeterDataDatabaseDao
) : MeterDataSource {

    override suspend fun insert(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.insert(meterData)
        }
    }

    override fun getObservableData(
        beginDate: Long,
        endDate: Long
    ) : LiveData<List<MeterData>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getMeterDataBetweenDates(beginDate, endDate)
        }
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        wrapEspressoIdlingResource {
            meterDataDao.insertPaidDate(paidDate)
        }
    }

    override fun getLastPaidDate(): LiveData<PaidDate> {
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

    override fun getPaidDatesRangeById(id: Int): LiveData<List<PaidDate>> {
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

    override suspend fun deleteMeterData(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.deleteMeterData(meterData)
        }
    }

    override suspend fun getLastMeterData(): MeterData? {
        wrapEspressoIdlingResource {
            return meterDataDao.getLastMeterData()
        }
    }

    override suspend fun insertPrice(price: Price) {
        wrapEspressoIdlingResource {
            meterDataDao.insertPrice(price)
        }
    }

    override fun getObservablePrice(): LiveData<Price> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePrice()
        }
    }

    override fun getObservablePriceCount(): LiveData<Int> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePriceCount()
        }
    }

    override suspend fun deletePrice() {
        wrapEspressoIdlingResource {
            meterDataDao.deletePrice()
        }
    }
}