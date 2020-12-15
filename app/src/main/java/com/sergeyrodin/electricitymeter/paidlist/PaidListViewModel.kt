package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class PaidListViewModel(dataSource: MeterDataSource) : ViewModel() {
    val paidDates = dataSource.getPaidDates()

    val noData = Transformations.map(paidDates) {
        it.isNullOrEmpty()
    }
}