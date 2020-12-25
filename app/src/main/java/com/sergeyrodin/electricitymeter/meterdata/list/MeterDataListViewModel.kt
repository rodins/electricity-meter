package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

private const val NO_PAID_DATE_ID = -1

class MeterDataListViewModel(
    private val dataSource: MeterDataSource,
    private val paidDateId: Int = NO_PAID_DATE_ID
) : ViewModel() {
    private val priceCalculator = PriceCalculator()
    private val kwhCalculator = KwhCalculator()

    private val observableData = MutableLiveData<List<MeterData>>()

    val dataToDisplay: LiveData<List<MeterDataPresentation>> =
        Transformations.map(observableData) { meterData ->
            convertMeterDataListToPresentationList(meterData)
        }

    private fun convertMeterDataListToPresentationList(meterData: List<MeterData>) =
        if (meterData.isNotEmpty()) {
            var prevData = -1
            val firstData = meterData.first().data
            meterData.map { currentData ->
                val dailyKw = kwhCalculator.calculateDailyKwh(prevData, currentData)
                prevData = currentData.data
                val dailyPrice = priceCalculator.calculateDailyPrice(dailyKw, currentData, firstData)
                MeterDataPresentation(currentData.data, currentData.date, dailyKw, dailyPrice)
            }
        } else {
            listOf()
        }

    val total: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.isEmpty()) {
            0
        } else {
            kwhCalculator.getTotalKwh(meterData)
        }
    }

    private fun getTotalKwh(meterData: List<MeterData>): Int {
        val first = meterData.first()
        val last = meterData.last()
        return last.data - first.data
    }

    val avg: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if (meterData.size < 2) {
            0
        } else {
            kwhCalculator.getAverageKwh(meterData)
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
        return priceCalculator.calculateTotalPrice(totalKwh)
    }

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }

    private val _hideKeyboardEvent = MutableLiveData<Event<Unit>>()
    val hideKeyboardEvent: LiveData<Event<Unit>>
       get() = _hideKeyboardEvent

    init{
        viewModelScope.launch {
            updateMeterData()
        }
    }

    private suspend fun updateMeterData() {
        if (paidDateId == NO_PAID_DATE_ID) {
            val paidDate = dataSource.getLastPaidDate()
            if (paidDate == null) {
                updateObservableData()
            } else {
                updateObservableData(paidDate.date)
            }
        } else {
            val paidDateRange = dataSource.getPaidDatesRangeById(paidDateId)
            if (paidDateRange?.size == 2) {
                updateObservableData(paidDateRange[0].date, paidDateRange[1].date)
            } else if (paidDateRange?.size == 1) {
                updateObservableData(paidDateRange[0].date)
            }
        }
    }

    private suspend fun updateObservableData(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE) {
        observableData.value = dataSource.getMeterDataBetweenDates(beginDate, endDate)
    }

    fun onAddData(data: String) {
        if (data.isNotBlank()) {
            val dataToInsert = data.toInt()
            val lastItemData = getLastItemData()
            if(lastItemData < dataToInsert) {
                viewModelScope.launch {
                    val meterData = MeterData(dataToInsert)
                    dataSource.insert(meterData)
                    updateMeterData()
                }
            }
            _hideKeyboardEvent.value = Event(Unit)
        }
    }

    private fun getLastItemData() = if (observableData.value?.isNotEmpty() == true) {
        observableData.value?.last()?.data ?: 0
    } else {
        0
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