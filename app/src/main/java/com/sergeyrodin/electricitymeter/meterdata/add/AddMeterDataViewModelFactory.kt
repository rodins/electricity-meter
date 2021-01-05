package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class AddMeterDataViewModelFactory(private val dataSource: MeterDataSource):
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddMeterDataViewModel::class.java)) {
            return AddMeterDataViewModel(dataSource) as T
        }else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}