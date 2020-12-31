package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

private const val NO_METER_DATA_ID = -1

class AddEditMeterDataViewModel(
    private val dataSource: MeterDataSource,
    private val meterDataId: Int = NO_METER_DATA_ID
) : ViewModel() {
    private val _saveMeterDataEvent = MutableLiveData<Event<Unit>>()
    val saveMeterDataEvent: LiveData<Event<Unit>>
        get() = _saveMeterDataEvent

    private val _data = MutableLiveData<String>()
    val data: LiveData<String>
        get() = _data

    private var meterData: MeterData? = null

    init {
        if(meterDataId != NO_METER_DATA_ID) {
            viewModelScope.launch {
                meterData = dataSource.getMeterDataById(meterDataId)
                meterData?.let {
                    _data.value = it.data.toString()
                }
            }
        }
    }

    fun onSaveMeterData(data: String) {
        val integerData = data.toIntOrNull()
        integerData?.let {
            if(meterData == null) {
                viewModelScope.launch {
                    val lastMeterData = dataSource.getMeterDataBetweenDates(0L, Long.MAX_VALUE)?.lastOrNull()
                    if(lastMeterData == null || lastMeterData.data < integerData) {
                        dataSource.insert(MeterData(integerData))
                        _saveMeterDataEvent.value = Event(Unit)
                    }
                }
            } else {
                meterData!!.data = integerData
                viewModelScope.launch {
                    dataSource.update(meterData!!)
                    _saveMeterDataEvent.value = Event(Unit)
                }
            }
        }
    }
}