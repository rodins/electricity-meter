package com.sergeyrodin.electricitymeter.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeterDataHistoryViewModel @Inject constructor(
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

    private val observablePrice = dataSource.getObservablePrice()
    private val observablePriceCount = dataSource.getObservablePriceCount()

    val calculator = MeterDataCalculator(observableData, observablePrice, observablePriceCount)

    fun start(paidDateId: Int) {
        paidDateInput.value = paidDateId
    }

    private fun getObservableMeterData(paidDateRange: List<PaidDate>) : LiveData<List<MeterData>> {
        val endDate = paidDateRange[0].date
        return if (paidDateRange.size == 2) {
            val beginDate = paidDateRange[1].date
            dataSource.getObservableData(beginDate, endDate)
        } else {
            dataSource.getObservableData(endDate = endDate)
        }
    }
}