package com.sergeyrodin.electricitymeter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
    private val observablePaidDate: LiveData<PaidDate> = Transformations.map(observablePaidDates) {
        it.lastOrNull()
    }

    private var meterDataId = 1
    private var paidDateId = 1

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

    override fun getObservableData(beginDate: Long, endDate: Long): LiveData<List<MeterData>> {
        val filteredData = data.filter {
            it.date in beginDate..endDate
        }
        data.clear()
        data.addAll(filteredData)
        observableData.value = data
        return observableData
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        testInsert(paidDate)
    }

    fun testInsert(paidDate: PaidDate) {
        if(paidDate.id == 0) {
            paidDate.id = paidDateId++
        }
        paidDates.add(paidDate)
        observablePaidDates.value = paidDates
    }

    override fun getLastPaidDate(): LiveData<PaidDate> {
        return observablePaidDate
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        paidDates.remove(paidDate)
        observablePaidDates.value = paidDates
    }

    override fun getPaidDates(): LiveData<List<PaidDate>> {
        return observablePaidDates
    }

    override fun getPaidDatesRangeById(id: Int): LiveData<List<PaidDate>> {
        val paidDatesRange = paidDates.filter {
            it.id >= id
        }

        val paidDatesRangeLimited = if(paidDatesRange.size <= 2) {
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

    override suspend fun getLastMeterData(): MeterData? {
        return data.lastOrNull()
    }

}