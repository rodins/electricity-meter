package com.sergeyrodin.electricitymeter.meterdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class FakeDataSource: MeterDataSource {
    private val data = mutableListOf<MeterData>()
    private val observableData = MutableLiveData<List<MeterData>>()
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
}