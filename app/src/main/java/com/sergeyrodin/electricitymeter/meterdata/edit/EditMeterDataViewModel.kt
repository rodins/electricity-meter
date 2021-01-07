package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.SaveMeterDataViewModel
import kotlinx.coroutines.launch

class EditMeterDataViewModel(
    private val dataSource: MeterDataSource,
    private val meterDataId: Int
) : SaveMeterDataViewModel() {
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

    override fun onSaveMeterData(data: Int) {
        meterData?.let {
            it.data = data
            viewModelScope.launch {
                dataSource.update(it)
                onSaveMeterData()
            }
        }
    }

    fun onDeleteMeterData() {
        meterData?.let {
            viewModelScope.launch {
                dataSource.deleteMeterData(it)
                onSaveMeterData()
            }
        }
    }
}