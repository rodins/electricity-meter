package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class AddEditMeterDataViewModelFactory(
    private val dataSource: MeterDataSource,
    private val meterDataId: Int
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddEditMeterDataViewModel::class.java)) {
            return AddEditMeterDataViewModel(dataSource, meterDataId) as T
        }else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}