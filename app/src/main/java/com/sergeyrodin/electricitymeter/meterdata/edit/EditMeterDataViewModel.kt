package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.SaveMeterDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMeterDataViewModel @Inject constructor(
    private val dataSource: MeterDataSource
) : SaveMeterDataViewModel() {
    private val _data = MutableLiveData<String>()
    val data: LiveData<String>
        get() = _data

    private var meterData: MeterData? = null

    fun start(meterDataId: Int) {
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
                dataSource.updateMeterData(it)
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