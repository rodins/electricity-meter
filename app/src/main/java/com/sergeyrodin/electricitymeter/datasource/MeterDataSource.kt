package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price

interface MeterDataSource {
    suspend fun insertMeterData(meterData: MeterData)
    fun getObservableMeterDataByDates(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE): LiveData<List<MeterData>>
    suspend fun insertPaidDate(paidDate: PaidDate)
    fun getLastObservablePaidDate(): LiveData<PaidDate>
    suspend fun deletePaidDate(paidDate: PaidDate?)
    fun getObservablePaidDates(): LiveData<List<PaidDate>>
    fun getObservablePaidDatesRangeById(id: Int): LiveData<List<PaidDate>>
    suspend fun deleteAllMeterData()
    suspend fun deleteAllPaidDates()
    suspend fun getMeterDataById(id: Int): MeterData?
    suspend fun updateMeterData(meterData: MeterData)
    suspend fun deleteMeterData(meterData: MeterData)
    suspend fun getLastMeterData(): MeterData?
    suspend fun insertPrice(price: Price)
    fun getFirstObservablePrice(): LiveData<Price>
    fun getObservablePriceCount(): LiveData<Int>
    suspend fun deletePrices()
    suspend fun getMeterDataByDate(date: Long): MeterData?
    suspend fun getFirstMeterData(): MeterData?
    suspend fun getPrice(): Price?
    fun getObservablePriceById(id: Int): LiveData<Price>
    fun getLastObservablePrice(): LiveData<Price>
    fun getObservablePrices(): LiveData<List<Price>>
    suspend fun deletePrice(price: Price)
    suspend fun getPaidDatesCountByPriceId(priceId: Int): Int
}