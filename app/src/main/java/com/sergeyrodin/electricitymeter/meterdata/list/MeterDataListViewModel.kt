package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.convertMeterDataListToPresentationList
import kotlinx.coroutines.launch

private const val NO_PAID_DATE_ID = -1

class MeterDataListViewModel(
    private val dataSource: MeterDataSource,
    private val paidDateId: Int = NO_PAID_DATE_ID
) : ViewModel() {
    private val observableData = MutableLiveData<List<MeterData>>()

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
        return com.sergeyrodin.electricitymeter.utils.calculateTotalPrice(totalKwh)
    }

    val noItems = Transformations.map(observableData) {
        it.isEmpty()
    }

    private val _addMeterDataEvent = MutableLiveData<Event<Int>>()
    val addMeterDataEvent: LiveData<Event<Int>>
       get() = _addMeterDataEvent

    val isPaidButtonVisible = Transformations.map(price) { price ->
        price > 0
    }

    init{
        viewModelScope.launch {
            updateMeterData()
        }
    }

    private suspend fun updateMeterData() {
        if (paidDateId == NO_PAID_DATE_ID) {
            updateMeterDataByDefault()
        } else {
            updateMeterDataByPaidDateId()
        }
    }

    private suspend fun updateMeterDataByDefault() {
        val paidDate = dataSource.getLastPaidDate()
        if (paidDate == null) {
            updateObservableData()
        } else {
            updateObservableData(paidDate.date)
        }
    }

    private suspend fun updateMeterDataByPaidDateId() {
        val paidDateRange = dataSource.getPaidDatesRangeById(paidDateId)
        if (paidDateRange?.size == 2) {
            updateObservableData(paidDateRange[0].date, paidDateRange[1].date)
        } else if (paidDateRange?.size == 1) {
            updateObservableData(paidDateRange[0].date)
        }
    }

    private suspend fun updateObservableData(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE) {
        observableData.value = dataSource.getMeterDataBetweenDates(beginDate, endDate)
    }

    fun onPaid() {
        observableData.value?.let { data ->
            if(data.isNotEmpty()) {
                val last = data.last()
                val paidDate = PaidDate(date = last.date)
                viewModelScope.launch{
                    dataSource.insertPaidDate(paidDate)
                    updateObservableData(last.date)
                }
            }
        }
    }

    fun onAddEditMeterData(id: Int) {
        _addMeterDataEvent.value = Event(id)
    }
}