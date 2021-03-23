package com.sergeyrodin.electricitymeter.history

import androidx.lifecycle.*
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
            dataSource.getObservablePaidDatesRangeById(paidDateId)
        }

    private val observableData =
        Transformations.switchMap(observablePaidDatesRange) { paidDatesRange ->
            getObservableMeterData(paidDatesRange)
        }

    private val observablePrice = observablePaidDatesRange.switchMap { paidDates ->
        val priceId = paidDates[0].priceId
        if(priceId == 0) {
            dataSource.getFirstObservablePrice()
        }else {
            dataSource.getObservablePriceById(priceId)
        }
    }

    private val observablePriceCount = dataSource.getObservablePriceCount()

    val calculator = MeterDataCalculator(observableData, observablePrice, observablePriceCount)

    fun start(paidDateId: Int) {
        paidDateInput.value = paidDateId
    }

    private fun getObservableMeterData(paidDateRange: List<PaidDate>): LiveData<List<MeterData>> {
        val endDate = paidDateRange[0].date
        return if (paidDateRange.size == 2) {
            val beginDate = paidDateRange[1].date
            dataSource.getObservableMeterDataByDates(beginDate, endDate)
        } else {
            dataSource.getObservableMeterDataByDates(endDate = endDate)
        }
    }
}