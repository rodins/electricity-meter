package com.sergeyrodin.electricitymeter.history

import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.CountingViewModel
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class MeterDataHistoryViewModel(
    private val dataSource: MeterDataSource,
    private val paidDateId: Int
) : CountingViewModel(dataSource) {

    init {
        viewModelScope.launch {
            updateMeterData()
        }
    }

    override suspend fun updateMeterData() {
        val paidDateRange = dataSource.getPaidDatesRangeById(paidDateId)
        if (paidDateRange?.size == 2) {
            updateObservableData(paidDateRange[0].date, paidDateRange[1].date)
        } else if (paidDateRange?.size == 1) {
            updateObservableData(paidDateRange[0].date)
        }
    }
}