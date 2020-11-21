package com.sergeyrodin.electricitymeter.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object DataHolder {
    private val data = mutableListOf<MeterData>()

    private val _observableData = MutableLiveData<List<MeterData>>()
    val observableData: LiveData<List<MeterData>>
        get() = _observableData

    fun insert(meterData: MeterData) {
        data.add(meterData)
        _observableData.value = data
    }

    fun clear() {
        data.clear()
        _observableData.value = data
    }
}