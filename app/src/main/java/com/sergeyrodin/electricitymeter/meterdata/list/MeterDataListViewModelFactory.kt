package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.database.MeterData

class MeterDataListViewModelFactory(private val data: MutableList<MeterData>): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MeterDataListViewModel::class.java)) {
            return MeterDataListViewModel(data) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}