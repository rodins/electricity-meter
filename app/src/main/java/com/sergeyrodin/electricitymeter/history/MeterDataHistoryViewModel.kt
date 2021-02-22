package com.sergeyrodin.electricitymeter.history

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator

class MeterDataHistoryViewModel @ViewModelInject constructor(
    private val dataSource: MeterDataSource
) : ViewModel() {

    private val paidDateInput = MutableLiveData<Int>()

    private val observablePaidDatesRange = Transformations
        .switchMap(paidDateInput) { paidDateId ->
            dataSource.getPaidDatesRangeById(paidDateId)
    }

    private val observableData = Transformations.switchMap(observablePaidDatesRange) { paidDatesRange ->
        getObservableMeterData(paidDatesRange)
    }

    val calculator = MeterDataCalculator(observableData)

    fun start(paidDateId: Int) {
        paidDateInput.value = paidDateId
    }

    private fun getObservableMeterData(paidDateRange: List<PaidDate>) : LiveData<List<MeterData>> {
        return if (paidDateRange.size == 2) {
            dataSource.getObservableData(paidDateRange[0].date, paidDateRange[1].date)
        } else {
            dataSource.getObservableData(paidDateRange[0].date)
        }
    }
}