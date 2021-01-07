package com.sergeyrodin.electricitymeter.meterdata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.Event

abstract class SaveMeterDataViewModel : ViewModel() {
    private val _saveMeterDataEvent = MutableLiveData<Event<Unit>>()
    val saveMeterDataEvent: LiveData<Event<Unit>>
        get() = _saveMeterDataEvent

    protected fun onSaveMeterData() {
        _saveMeterDataEvent.value = Event(Unit)
    }

    fun onSaveMeterData(data: String) {
        val integerData = data.toIntOrNull()
        integerData?.let {
            onSaveMeterData(it)
        }
    }

    protected abstract fun onSaveMeterData(data: Int)
}