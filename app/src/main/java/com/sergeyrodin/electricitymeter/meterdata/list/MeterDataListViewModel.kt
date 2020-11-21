package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.database.MeterData

class MeterDataListViewModel(): ViewModel(){
    val dataToDisplay = DataHolder.observableData

    fun onAddData(data: String) {
        if(data.isNotBlank()) {
            val meterData = MeterData(data.toInt())
            DataHolder.insert(meterData)
        }
    }
}