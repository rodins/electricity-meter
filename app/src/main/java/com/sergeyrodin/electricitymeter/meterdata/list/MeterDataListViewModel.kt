package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

private const val PRICE_KWH_SMALL = 0.9
private const val PRICE_KWH_BIG = 1.68
private const val SMALL_PRICE_KW = 100
private const val PRICE_100_KWH = PRICE_KWH_SMALL * SMALL_PRICE_KW

class MeterDataListViewModel(
    private val dataSource: MeterDataSource,
    private val dateOfMonthToDisplay: Long = System.currentTimeMillis()
) : ViewModel() {
    private val observableData = dataSource.getMonthMeterData(dateOfMonthToDisplay)
    val dataToDisplay: LiveData<List<MeterDataPresentation>> =
        Transformations.map(observableData) { meterData ->
            if (meterData.isNotEmpty()) {
                var prevData = -1
                val firstData = meterData.first().data
                meterData.map {
                    val dailyKw = if (prevData != -1) it.data - prevData else 0
                    prevData = it.data
                    var price = 0.0
                    if (dailyKw > 0) {
                        val currentTotalKw = it.data - firstData
                        if (currentTotalKw > SMALL_PRICE_KW) {
                            if (currentTotalKw - dailyKw > SMALL_PRICE_KW) {
                                price = dailyKw * PRICE_KWH_BIG
                            } else {
                                val bigPriceKw = currentTotalKw - SMALL_PRICE_KW
                                val smallPriceKw = dailyKw - bigPriceKw
                                val smallPrice = smallPriceKw * PRICE_KWH_SMALL
                                val bigPrice = bigPriceKw * PRICE_KWH_BIG
                                price = smallPrice + bigPrice
                            }
                        } else {
                            price = dailyKw * PRICE_KWH_SMALL
                        }
                    }
                    MeterDataPresentation(it.data, it.date, dailyKw, price)
                }
            } else {
                listOf()
            }
        }

    val total: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.isEmpty()) {
            0
        } else {
            getTotal(meterData)
        }
    }

    private fun getTotal(meterData: List<MeterData>): Int {
        val first = meterData.first()
        val last = meterData.last()
        return last.data - first.data
    }

    val avg: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.size < 2) {
            0
        } else {
            val total = getTotal(meterData)
            val numberOfItems = meterData.size - 1
            total / numberOfItems
        }
    }

    val price: LiveData<Double> = Transformations.map(observableData) { meterData ->
        if (meterData.size < 2) {
            0.0
        } else {
            calculatePrice(meterData)
        }
    }

    private fun calculatePrice(meterData: List<MeterData>): Double {
        val total = getTotal(meterData)
        if (total > 100) {
            return (total - 100) * PRICE_KWH_BIG + PRICE_100_KWH
        } else {
            return total * PRICE_KWH_SMALL
        }
    }

    fun onAddData(data: String) {
        if (data.isNotBlank()) {
            viewModelScope.launch {
                val meterData = MeterData(data.toInt())
                dataSource.insert(meterData)
            }
        }
    }
}