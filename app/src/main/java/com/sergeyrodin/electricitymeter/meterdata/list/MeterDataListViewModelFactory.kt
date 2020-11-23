package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import java.lang.IllegalArgumentException

class MeterDataListViewModelFactory(private val dataSource: MeterDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MeterDataListViewModel::class.java)) {
            return MeterDataListViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}