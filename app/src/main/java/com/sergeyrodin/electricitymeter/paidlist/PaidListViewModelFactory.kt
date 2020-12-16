package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class PaidListViewModelFactory(private val dataSource: MeterDataSource): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PaidListViewModel::class.java)) {
            return PaidListViewModel(dataSource) as T
        } else {
            throw IllegalArgumentException("Unknown view model class")
        }
    }
}