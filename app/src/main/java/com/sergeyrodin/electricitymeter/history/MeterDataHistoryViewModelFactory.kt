package com.sergeyrodin.electricitymeter.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class MeterDataHistoryViewModelFactory(
    private val dataSource: MeterDataSource,
    private val paidDateId: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeterDataHistoryViewModel::class.java)) {
            return MeterDataHistoryViewModel(dataSource, paidDateId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}