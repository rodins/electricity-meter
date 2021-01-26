package com.sergeyrodin.electricitymeter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDataSource @Inject constructor(): MeterDataSource {

    private val data = mutableListOf<MeterData>()
    private val observableData = MutableLiveData<List<MeterData>>()
    private val paidDates = mutableListOf<PaidDate>()
    private val observablePaidDates = MutableLiveData<List<PaidDate>>()

    private var meterDataId = 1

    init{
        observableData.value = data
        observablePaidDates.value = paidDates
    }

    override suspend fun insert(meterData: MeterData) {
        testInsert(meterData)
    }

    fun testInsert(meterData: MeterData) {
        if(meterData.id == 0) {
            meterData.id = meterDataId++
        }
        data.add(meterData)
        observableData.value = data
    }

    override suspend fun getMeterDataBetweenDates(beginDate: Long, endDate: Long): List<MeterData>? {
        return data.filter {
            it.date in beginDate..endDate
        }
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        testInsert(paidDate)
    }

    fun testInsert(paidDate: PaidDate) {
        paidDates.add(paidDate)
        observablePaidDates.value = paidDates
    }

    override suspend fun getLastPaidDate(): PaidDate? {
        return paidDates.lastOrNull()
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        paidDates.remove(paidDate)
        observablePaidDates.value = paidDates
    }

    fun testPaidDatesSize() = paidDates.size

    override fun getPaidDates(): LiveData<List<PaidDate>> {
        return observablePaidDates
    }

    override suspend fun getPaidDatesRangeById(id: Int): List<PaidDate>? {
        val paidDatesRange = paidDates.filter {
            it.id >= id
        }

        return if(paidDatesRange.size <= 2) {
            paidDatesRange
        } else {
            paidDatesRange.subList(0, 2)
        }
    }

    override suspend fun deleteAllMeterData() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPaidDates() {
        paidDates.clear()
    }

    fun  getMeterDataForTest(): List<MeterData> {
        return data
    }

    override suspend fun getMeterDataById(id: Int): MeterData? {
        return data.find {
            it.id == id
        }?.copy()
    }

    override suspend fun update(meterData: MeterData) {
        val oldMeterData = data.find {
            it.id == meterData.id
        }
        val index = data.indexOf(oldMeterData)
        data[index] = meterData
    }

    override suspend fun deleteMeterData(meterData: MeterData) {
        data.remove(meterData)
    }

}