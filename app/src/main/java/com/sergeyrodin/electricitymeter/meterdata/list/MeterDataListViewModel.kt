package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class MeterDataListViewModel(private val dataSource: MeterDataSource): ViewModel(){
    val dataToDisplay = dataSource.getMeterData()

    fun onAddData(data: String) {
        if(data.isNotBlank()) {
            viewModelScope.launch {
                val meterData = MeterData(data.toInt())
                dataSource.insert(meterData)
            }
        }
    }
}