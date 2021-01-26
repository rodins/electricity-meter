package com.sergeyrodin.electricitymeter.history

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import kotlinx.coroutines.launch

class MeterDataHistoryViewModel @ViewModelInject constructor(
    val calculator: MeterDataCalculator,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun start(paidDateId: Int) {
        viewModelScope.launch {
            updateMeterData(paidDateId)
        }
    }

    private suspend fun updateMeterData(paidDateId: Int) {
        val paidDateRange = calculator.dataSource.getPaidDatesRangeById(paidDateId)
        if (paidDateRange?.size == 2) {
            calculator.updateObservableData(paidDateRange[0].date, paidDateRange[1].date)
        } else if (paidDateRange?.size == 1) {
            calculator.updateObservableData(paidDateRange[0].date)
        }
    }
}