package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.CountingViewModel
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class MeterDataListViewModel(
    private val dataSource: MeterDataSource,
) : CountingViewModel(dataSource) {

    private val _addMeterDataEvent = MutableLiveData<Event<Unit>>()
    val addMeterDataEvent: LiveData<Event<Unit>>
        get() = _addMeterDataEvent

    private val _editMeterDataEvent = MutableLiveData<Event<Int>>()
    val editMeterDataEvent: LiveData<Event<Int>>
       get() = _editMeterDataEvent

    val isPaidButtonVisible = Transformations.map(price) { price ->
        price > 0
    }

    init {
        viewModelScope.launch {
            updateMeterData()
        }
    }

    override suspend fun updateMeterData() {
        val paidDate = dataSource.getLastPaidDate()
        if (paidDate == null) {
            updateObservableData()
        } else {
            updateObservableData(paidDate.date)
        }
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

    fun onEditMeterData(id: Int) {
        _editMeterDataEvent.value = Event(id)
    }

    fun onAddMeterData() {
        _addMeterDataEvent.value = Event(Unit)
    }
}