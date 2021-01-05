package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class EditMeterDataViewModel(
    private val dataSource: MeterDataSource,
    private val meterDataId: Int
) : ViewModel() {
    private val _saveMeterDataEvent = MutableLiveData<Event<Unit>>()
    val saveMeterDataEvent: LiveData<Event<Unit>>
        get() = _saveMeterDataEvent

    private val _data = MutableLiveData<String>()
    val data: LiveData<String>
        get() = _data

    private var meterData: MeterData? = null

    init {
        viewModelScope.launch {
            meterData = dataSource.getMeterDataById(meterDataId)
            meterData?.let {
                _data.value = it.data.toString()
            }
        }
    }

    fun onSaveMeterData(data: String) {
        val integerData = data.toIntOrNull()
        integerData?.let {
            meterData?.let {
                it.data = integerData
                viewModelScope.launch {
                    dataSource.update(it)
                    _saveMeterDataEvent.value = Event(Unit)
                }
            }
        }
    }

    fun onDeleteMeterData() {
        meterData?.let {
            viewModelScope.launch {
                dataSource.deleteMeterData(it)
                _saveMeterDataEvent.value = Event(Unit)
            }
        }
    }
}