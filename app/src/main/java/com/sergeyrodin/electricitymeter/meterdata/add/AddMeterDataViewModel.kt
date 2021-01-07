package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.SaveMeterDataViewModel
import kotlinx.coroutines.launch

class AddMeterDataViewModel(private val dataSource: MeterDataSource) : SaveMeterDataViewModel() {
    override fun onSaveMeterData(data: Int) {
        viewModelScope.launch {
            val lastMeterData =
                dataSource.getMeterDataBetweenDates(0L, Long.MAX_VALUE)?.lastOrNull()
            if (lastMeterData == null || lastMeterData.data < data) {
                dataSource.insert(MeterData(data))
                onSaveMeterData()
            }
        }
    }
}