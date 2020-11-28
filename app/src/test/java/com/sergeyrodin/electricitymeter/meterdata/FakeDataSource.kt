package com.sergeyrodin.electricitymeter.meterdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.getMonthBoundariesByDate
import java.util.*

class FakeDataSource: MeterDataSource {
    private val data = mutableListOf<MeterData>()
    private val observableData = MutableLiveData<List<MeterData>>()

    init{
        observableData.value = data
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


}