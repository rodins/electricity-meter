package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class MeterDataListViewModel(private val dataSource: MeterDataSource): ViewModel(){
    private val observableData = dataSource.getMeterData()
    val dataToDisplay: LiveData<List<MeterDataPresentation>> = Transformations.map(observableData) { meterData ->
        var prevData = -1
        meterData.map {
            val diff = if(prevData != -1) it.data - prevData else 0
            prevData = it.data
            MeterDataPresentation(it.data, it.date, diff)
        }
    }

    val total: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if(meterData.isEmpty()) {
            0
        }else{
            getTotal(meterData)
        }
    }

    private fun getTotal(meterData: List<MeterData>): Int {
        val first = meterData.first()
        val last = meterData.last()
        return last.data - first.data
    }

    val avg: LiveData<Int> = Transformations.map(observableData) { meterData ->
        if(meterData.size < 2) {
            0
        }else{
            val total = getTotal(meterData)
            val numberOfItems = meterData.size - 1
            total/numberOfItems
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