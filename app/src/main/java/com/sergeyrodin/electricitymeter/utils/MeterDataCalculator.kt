package com.sergeyrodin.electricitymeter.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation
import com.sergeyrodin.electricitymeter.meterdata.list.getAverageKwh
import com.sergeyrodin.electricitymeter.meterdata.list.getTotalKwh
import javax.inject.Inject

class MeterDataCalculator @Inject constructor(val dataSource: MeterDataSource) {
    val observableData = MutableLiveData<List<MeterData>>()

    val dataToDisplay: LiveData<List<MeterDataPresentation>> =
        Transformations.map(observableData) { meterData ->
            convertMeterDataListToPresentationList(meterData)
        }

    val total: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.isEmpty()) {
            0
        } else {
            getTotalKwh(meterData)
        }
    }

    val avg: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.size < 2) {
            0
        } else {
            getAverageKwh(meterData)
        }
    }

    val price: LiveData<Double> = Transformations.map(observableData) { meterData ->
        if (meterData.size < 2) {
            0.0
        } else {
            calculateTotalPrice(meterData)
        }
    }

    private fun calculateTotalPrice(meterData: List<MeterData>): Double {
        val totalKwh = getTotalKwh(meterData)
        return calculateTotalPrice(totalKwh)
    }

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }

    suspend fun updateObservableData(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE) {
        observableData.value = dataSource.getMeterDataBetweenDates(beginDate, endDate)
    }
}