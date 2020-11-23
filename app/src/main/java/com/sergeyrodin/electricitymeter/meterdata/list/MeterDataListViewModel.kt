package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class MeterDataListViewModel(private val dataSource: MeterDataSource): ViewModel(){
    private val observableData = dataSource.getMeterData()
    val dataToDisplay = Transformations.map(observableData) { meterData ->
        var prevData = -1
        meterData.map {
            val diff = if(prevData != -1) it.data - prevData else 0
            prevData = it.data
            MeterDataPresentation(it.data, it.date, diff)
        }
    }

    fun onAddData(data: String) {
        if(data.isNotBlank()) {
            viewModelScope.launch {
                val meterData = MeterData(data.toInt())
                dataSource.insert(meterData)
            }
        }
    }
}