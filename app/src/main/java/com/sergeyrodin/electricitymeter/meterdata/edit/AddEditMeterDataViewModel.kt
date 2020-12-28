package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class AddEditMeterDataViewModel(private val dataSource: MeterDataSource) : ViewModel() {
    private val _saveMeterDataEvent = MutableLiveData<Event<Unit>>()
    val saveMeterDataEvent: LiveData<Event<Unit>>
        get() = _saveMeterDataEvent

    fun onSaveMeterData(data: String) {
        val integerData = data.toIntOrNull()
        integerData?.let {
            viewModelScope.launch {
                val lastMeterData = dataSource.getMeterDataBetweenDates(0L, Long.MAX_VALUE)?.lastOrNull()
                if(lastMeterData == null || lastMeterData.data < integerData) {
                    dataSource.insert(MeterData(integerData))
                    _saveMeterDataEvent.value = Event(Unit)
                }
            }
        }
    }
}