package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.DataHolder

class MeterDataInputViewModel: ViewModel() {
    private val _saveDataEvent = MutableLiveData<Event<Unit>>()
    val saveDataEvent: LiveData<Event<Unit>>
        get() = _saveDataEvent

    fun onSaveData(data: String) {
        DataHolder.data.add(data)
        _saveDataEvent.value = Event(Unit)
    }
}