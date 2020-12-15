package com.sergeyrodin.electricitymeter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.getMonthBoundariesByDate

class FakeDataSource: MeterDataSource {
    private val data = mutableListOf<MeterData>()
    private val observableData = MutableLiveData<List<MeterData>>()
    private val paidDates = mutableListOf<PaidDate>()
    private val observablePaidDates = MutableLiveData<List<PaidDate>>()

    init{
        observableData.value = data
        observablePaidDates.value = paidDates
    }

    override suspend fun insert(meterData: MeterData) {
        testInsert(meterData)
    }

    fun testInsert(meterData: MeterData) {
        data.add(meterData)
        observableData.value = data
    }

    override fun getMeterData(): LiveData<List<MeterData>> {
        return observableData
    }

    override fun getMonthMeterData(dateOfMonthToDisplay: Long): LiveData<List<MeterData>> {
        if(dateOfMonthToDisplay == -1L) {
            return observableData
        }else{
            val boundaries = getMonthBoundariesByDate(dateOfMonthToDisplay)

            val dataOfMonth = data.filter {
                it.date in boundaries.lastDayOfPrevMonthMillis..boundaries.lastDayOfCurrentMonthMillis
            }
            observableData.value = dataOfMonth
            return observableData
        }
    }

    override suspend fun getMeterDataBetweenDates(beginDate: Long, endDate: Long): List<MeterData>? {
        return data.filter {
            it.date in beginDate until endDate
        }
    }

    override suspend fun insertPaidDate(paidDate: PaidDate) {
        testInsert(paidDate)
    }

    fun testInsert(paidDate: PaidDate) {
        paidDates.add(paidDate)
        observablePaidDates.value = paidDates
    }

    override suspend fun getPaidDate(): PaidDate? {
        return paidDates.lastOrNull()
    }

    override suspend fun deletePaidDate(paidDate: PaidDate?) {
        paidDates.remove(paidDate)
    }

    fun testPaidDatesSize() = paidDates.size

    override fun getPaidDates(): LiveData<List<PaidDate>> {
        return observablePaidDates
    }

}