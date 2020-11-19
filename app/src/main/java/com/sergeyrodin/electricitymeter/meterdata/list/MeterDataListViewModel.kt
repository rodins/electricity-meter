package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.Event

class MeterDataListViewModel(data: String): ViewModel(){
    val dataToDisplay = data

    private val _addDataEvent = MutableLiveData<Event<Unit>>()
    val addDataEvent: LiveData<Event<Unit>>
        get() = _addDataEvent

    fun onAddData() {
        _addDataEvent.value = Event(Unit)
    }
}