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

    override suspend fun insertMeterData(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.insertMeterData(meterData)
        }
    }

    override fun getObservableMeterDataByDates(
        beginDate: Long,
        endDate: Long
    ) : LiveData<List<MeterData>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservableMeterDataBetweenDates(beginDate, endDate)
        }
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        wrapEspressoIdlingResource {
            meterDataDao.insertPaidDate(paidDate)
        }
    }

    override fun getLastObservablePaidDate(): LiveData<PaidDate> {
        wrapEspressoIdlingResource {
            return meterDataDao.getLastObservablePaidDate()
        }
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        wrapEspressoIdlingResource {
            meterDataDao.deletePaidDate(paidDate)
        }
    }

    override fun getObservablePaidDates(): LiveData<List<PaidDate>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePaidDates()
        }
    }

    override fun getObservablePaidDatesRangeById(id: Int): LiveData<List<PaidDate>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePaidDatesRangeById(id)
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

    override suspend fun updateMeterData(meterData: MeterData) {
        wrapEspressoIdlingResource {
            meterDataDao.updateMeterData(meterData)
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

    override fun getFirstObservablePrice(): LiveData<Price> {
        wrapEspressoIdlingResource {
            return meterDataDao.getFirstObservablePrice()
        }
    }

    override fun getObservablePriceCount(): LiveData<Int> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePriceCount()
        }
    }

    override suspend fun deletePrices() {
        wrapEspressoIdlingResource {
            meterDataDao.deletePrices()
        }
    }

    override suspend fun getMeterDataByDate(date: Long): MeterData? {
        wrapEspressoIdlingResource {
            return meterDataDao.getMeterDataByDate(date)
        }
    }

    override suspend fun getFirstMeterData(): MeterData? {
        wrapEspressoIdlingResource {
            return meterDataDao.getFirstMeterData()
        }
    }

    override suspend fun getPrice(): Price? {
        wrapEspressoIdlingResource {
            return meterDataDao.getPrice()
        }
    }

    override fun getObservablePriceById(id: Int): LiveData<Price> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePriceById(id)
        }
    }

    override fun getLastObservablePrice(): LiveData<Price> {
        wrapEspressoIdlingResource {
            return meterDataDao.getLastObservablePrice()
        }
    }

    override fun getObservablePrices(): LiveData<List<Price>> {
        wrapEspressoIdlingResource {
            return meterDataDao.getObservablePrices()
        }
    }

    override suspend fun deletePrice(price: Price) {
        wrapEspressoIdlingResource {
            meterDataDao.deletePrice(price)
        }
    }

    override suspend fun getPaidDatesCountByPriceId(priceId: Int): Int {
        wrapEspressoIdlingResource {
            return meterDataDao.getPaidDatesCountByPriceId(priceId)
        }
    }
}