package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.database.MeterData

class MeterDataInputViewModel: ViewModel() {
    private val _saveDataEvent = MutableLiveData<Event<Unit>>()
    val saveDataEvent: LiveData<Event<Unit>>
        get() = _saveDataEvent

    fun onSaveData(data: String) {
        if(data.isNotBlank()) {
            val meterData = MeterData(data.toInt())
            DataHolder.data.add(meterData)
            _saveDataEvent.value = Event(Unit)
        }
    }
}