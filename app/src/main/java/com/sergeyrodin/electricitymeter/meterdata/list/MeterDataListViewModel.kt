package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

private const val PRICE_KWH_SMALL = 0.9
private const val PRICE_KWH_BIG = 1.68
private const val SMALL_PRICE_KW = 100
private const val PRICE_100_KWH = PRICE_KWH_SMALL * SMALL_PRICE_KW

class MeterDataListViewModel(private val dataSource: MeterDataSource) : ViewModel() {
    private val observableData = MutableLiveData<List<MeterData>>()
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

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }

    private val _hideKeyboardEvent = MutableLiveData<Event<Unit>>()
    val hideKeyboardEvent: LiveData<Event<Unit>>
       get() = _hideKeyboardEvent

    init{
        viewModelScope.launch{
            val paidDate = dataSource.getPaidDate()
            if(paidDate == null) {
                updateObservableData()
            }else {
                updateObservableData(paidDate.date)
            }
        }
    }

    fun onAddData(data: String) {
        if (data.isNotBlank()) {
            val dataToInsert = data.toInt()
            val lastItemData = if(observableData.value?.isNotEmpty() == true) {
                observableData.value?.last()?.data?:0
            }else{
                0
            }
            if(lastItemData < dataToInsert) {
                viewModelScope.launch {
                    val meterData = MeterData(dataToInsert)
                    dataSource.insert(meterData)
                    updateObservableData()
                }
            }
            _hideKeyboardEvent.value = Event(Unit)
        }
    }

    private suspend fun updateObservableData(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE) {
        observableData.value = dataSource.getMeterDataBetweenDates(beginDate, endDate)
    }

    fun onPaid() {
        observableData.value?.let { data ->
            if(data.isNotEmpty()) {
                viewModelScope.launch{
                    val last = data.last()
                    val paidDate = PaidDate(date = last.date)
                    dataSource.insertPaidDate(paidDate)
                    updateObservableData(last.date)
                }
            }
        }
    }
}