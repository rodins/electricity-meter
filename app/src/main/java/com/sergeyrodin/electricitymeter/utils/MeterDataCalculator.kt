package com.sergeyrodin.electricitymeter.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation
import com.sergeyrodin.electricitymeter.meterdata.list.getAverageKwh
import com.sergeyrodin.electricitymeter.meterdata.list.getTotalKwh

private const val PROGNOSIS_DAYS_NUMBER = 30

class MeterDataCalculator (observableData: LiveData<List<MeterData>>, reversed: Boolean = false) {

    val dataToDisplay: LiveData<List<MeterDataPresentation>> =
        Transformations.map(observableData) { meterData ->
            if(reversed) {
                convertMeterDataListToPresentationListReversed(meterData)
            } else {
                convertMeterDataListToPresentationList(meterData)
            }
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

    val prognosis = Transformations.map(avg) { average ->
        val prognosisKwh = average * PROGNOSIS_DAYS_NUMBER
        calculateTotalPrice(prognosisKwh)
    }

    private fun calculateTotalPrice(meterData: List<MeterData>): Double {
        val totalKwh = getTotalKwh(meterData)
        return calculateTotalPrice(totalKwh)
    }

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }
}