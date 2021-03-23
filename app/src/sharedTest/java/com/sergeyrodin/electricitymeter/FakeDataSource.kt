package com.sergeyrodin.electricitymeter

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataSource @Inject constructor() : MeterDataSource {

    private val data = mutableListOf<MeterData>()
    private val observableData = MutableLiveData<List<MeterData>>()
    private val paidDates = mutableListOf<PaidDate>()
    private val observablePaidDates = MutableLiveData<List<PaidDate>>()
    private val observablePaidDate: LiveData<PaidDate> = Transformations.map(observablePaidDates) {
        it.lastOrNull()
    }

    private var meterDataId = 1
    private var paidDateId = 1

    private val prices = mutableListOf<Price>()
    private val observablePrices = MutableLiveData<List<Price>>()
    private val firstObservablePrice = observablePrices.map {
        it.first()
    }
    private val lastObservablePrice = observablePrices.map {
        it.last()
    }
    private val observablePriceCount = observablePrices.map {
        it.size
    }

    private val observablePrice = MutableLiveData<Price>()

    init {
        observableData.value = data
        observablePaidDates.value = paidDates
        observablePrices.value = prices
    }

    override suspend fun insertMeterData(meterData: MeterData) {
        insertMeterDataBlocking(meterData)
    }

    fun insertMeterDataBlocking(meterData: MeterData) {
        if (meterData.id == 0) {
            meterData.id = meterDataId++
        }
        data.add(meterData)
        observableData.value = data
    }

    override fun getObservableMeterDataByDates(
        beginDate: Long,
        endDate: Long
    ): LiveData<List<MeterData>> {
        val filteredData = data.filter {
            it.date in beginDate..endDate
        }
        data.clear()
        data.addAll(filteredData)
        observableData.value = data
        return observableData
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        insertPaidDateBlocking(paidDate)
    }

    fun insertPaidDateBlocking(paidDate: PaidDate) {
        val index = prices.indexOfFirst {
            it.id == paidDate.priceId
        }

        if (index == -1) throw SQLiteConstraintException()

        if (paidDate.id == 0) {
            paidDate.id = paidDateId++
        }
        insertPaidDateBlockingMigrated(paidDate)
    }

    override fun getLastObservablePaidDate(): LiveData<PaidDate> {
        return observablePaidDate
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        paidDates.remove(paidDate)
        observablePaidDates.value = paidDates
    }

    override fun getObservablePaidDates(): LiveData<List<PaidDate>> {
        return observablePaidDates
    }

    override fun getObservablePaidDatesRangeById(id: Int): LiveData<List<PaidDate>> {
        val paidDatesRange = paidDates.filter {
            it.id <= id
        }.reversed()

        val paidDatesRangeLimited = if (paidDatesRange.size <= 2) {
            paidDatesRange
        } else {
            paidDatesRange.subList(0, 2)
        }

        observablePaidDates.value = paidDatesRangeLimited
        return observablePaidDates
    }

    override suspend fun deleteAllMeterData() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPaidDates() {
        paidDates.clear()
    }

    fun getMeterDataForTest(): List<MeterData> {
        return data
    }

    override suspend fun getMeterDataById(id: Int): MeterData? {
        return data.find {
            it.id == id
        }?.copy()
    }

    override suspend fun updateMeterData(meterData: MeterData) {
        val oldMeterData = data.find {
            it.id == meterData.id
        }
        val index = data.indexOf(oldMeterData)
        data[index] = meterData
    }

    override suspend fun deleteMeterData(meterData: MeterData) {
        data.remove(meterData)
    }

    override suspend fun getLastMeterData(): MeterData? {
        return data.lastOrNull()
    }

    override suspend fun insertPrice(price: Price) {
        insertPriceBlocking(price)
    }

    fun insertPriceBlocking(price: Price) {
        if(prices.contains(price)) throw  SQLiteConstraintException()

        prices.add(price)

        observablePrices.value = prices
    }

    override fun getFirstObservablePrice(): LiveData<Price> {
        return firstObservablePrice
    }

    fun getFirstPriceBlocking(): Price? {
        if (prices.isNotEmpty()) {
            return prices.first()
        }
        return null
    }

    fun getLastPriceBlocking(): Price? {
        if (prices.isNotEmpty()) {
            return prices.last()
        }
        return null
    }

    override fun getObservablePriceCount(): LiveData<Int> {
        return observablePriceCount
    }

    override suspend fun deletePrices() {
        deletePricesBlocking()
    }

    fun deletePricesBlocking() {
        prices.clear()
        observablePrices.value = prices
    }

    override suspend fun getMeterDataByDate(date: Long): MeterData? {
        return data.find {
            it.date == date
        }
    }

    override suspend fun getFirstMeterData(): MeterData? {
        return data.first()
    }

    override suspend fun getPrice(): Price? {
        return getFirstPriceBlocking()
    }

    override fun getObservablePriceById(id: Int): LiveData<Price> {
        val price = prices.find { price ->
            price.id == id
        }
        observablePrice.value = price
        return observablePrice
    }

    override fun getLastObservablePrice(): LiveData<Price> {
        return lastObservablePrice
    }

    override fun getObservablePrices(): LiveData<List<Price>> {
        return observablePrices
    }

    override suspend fun deletePrice(price: Price) {
        prices.remove(price)
        observablePrices.value = prices
    }

    override suspend fun getPaidDatesCountByPriceId(priceId: Int): Int {
        return paidDates.filter { paidDate ->
            paidDate.priceId == priceId
        }.size
    }

    fun insertPaidDateBlockingMigrated(paidDate: PaidDate) {
        paidDates.add(paidDate)
        observablePaidDates.value = paidDates
    }

}