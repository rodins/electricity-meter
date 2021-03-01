package com.sergeyrodin.electricitymeter.utils

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.meterdata.list.MeterDataPresentation

class MeterDataCalculator(
    observableData: LiveData<List<MeterData>>,
    private val observablePrice: LiveData<Price>,
    private val observablePriceCount: LiveData<Int>,
    private val reversed: Boolean = false,
) {

    val dataToDisplay: LiveData<List<MeterDataPresentation>> =
        convertDataToDisplay(observableData)

    private fun convertDataToDisplay(observableData: LiveData<List<MeterData>>) =
        Transformations.switchMap(observableData) { meterData ->
            observablePriceCount.switchMap { count ->
                if(priceIsSet(count)) {
                    convertDataToDisplayKwhPriceSet(meterData)
                } else {
                    emptyObservableList()
                }
            }
        }

    private fun convertDataToDisplayKwhPriceSet(meterData: List<MeterData>) =
        Transformations.map(observablePrice) { priceKwh ->
            if (reversed) {
                convertMeterDataListToPresentationListReversed(meterData, priceKwh.price)
            } else {
                convertMeterDataListToPresentationList(meterData, priceKwh.price)
            }
        }

    private fun emptyObservableList(): MutableLiveData<List<MeterDataPresentation>> =
        MutableLiveData(listOf())

    val total: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.isEmpty()) {
            0
        } else {
            getTotalKwh(meterData)
        }
    }

    val avg: LiveData<Int> = Transformations.map(observableData) { meterData ->
        calculateAvgByDates(meterData)
    }

    val price: LiveData<Double> = Transformations.switchMap(observableData) { meterData ->
        calculatePrice(observablePriceCount, meterData)
    }

    private fun calculatePrice(
        observablePriceCount: LiveData<Int>,
        meterData: List<MeterData>
    ) = observablePriceCount.switchMap { count ->
        if (priceIsSet(count)) {
            calculateTotalPriceKwhPriceSet(meterData)
        } else {
            zeroDoubleValue()
        }
    }

    private fun priceIsSet(count: Int) = count != 0

    private fun calculateTotalPriceKwhPriceSet(meterData: List<MeterData>) =
        observablePrice.map { priceKwh ->
            if (notEnoughItems(meterData)) {
                0.0
            } else {
                calculateTotalPrice(meterData, priceKwh.price)
            }
        }

    private fun notEnoughItems(meterData: List<MeterData>) =
        meterData.size < 2

    private fun calculateTotalPrice(meterData: List<MeterData>, priceKwh: Double): Double {
        val totalKwh = getTotalKwh(meterData)
        return calculateTotalPrice(totalKwh, priceKwh)
    }

    private fun zeroDoubleValue() = MutableLiveData(0.0)

    val prognosis = Transformations.switchMap(observableData) { meterData ->
        calculatePrognosis(observablePriceCount, meterData)
    }

    private fun calculatePrognosis(
        observablePriceCount: LiveData<Int>,
        meterData: List<MeterData>
    ) = observablePriceCount.switchMap { count ->
        if (priceIsSet(count)) {
            calculatePrognosisKwhPriceSet(meterData)
        } else {
            zeroDoubleValue()
        }
    }

    private fun calculatePrognosisKwhPriceSet(meterData: List<MeterData>) =
        observablePrice.map { priceKwh ->
            calculatePrognosisByDates(meterData, priceKwh.price)
        }

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }
}