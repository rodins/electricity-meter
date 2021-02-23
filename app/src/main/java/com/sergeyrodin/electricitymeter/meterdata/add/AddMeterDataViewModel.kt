package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.SaveMeterDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMeterDataViewModel @Inject constructor(
    private val dataSource: MeterDataSource
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