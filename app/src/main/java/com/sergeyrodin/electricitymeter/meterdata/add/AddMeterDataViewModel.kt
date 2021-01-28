package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.SaveMeterDataViewModel
import kotlinx.coroutines.launch

class AddMeterDataViewModel @ViewModelInject constructor(
    private val dataSource: MeterDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle
    ) : SaveMeterDataViewModel() {

    override fun onSaveMeterData(data: Int) {
        viewModelScope.launch {
            val lastMeterData = dataSource.getLastMeterData()
            if (lastMeterData == null || lastMeterData.data < data) {
                dataSource.insert(MeterData(data))
                onSaveMeterData()
            }
        }
    }

}