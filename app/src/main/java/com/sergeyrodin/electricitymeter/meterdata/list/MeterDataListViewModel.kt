package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeterDataListViewModel @Inject constructor(
    private val dataSource: MeterDataSource
) : ViewModel() {

    private val _addMeterDataEvent = MutableLiveData<Event<Unit>>()
    val addMeterDataEvent: LiveData<Event<Unit>>
        get() = _addMeterDataEvent

    private val _editMeterDataEvent = MutableLiveData<Event<Int>>()
    val editMeterDataEvent: LiveData<Event<Int>>
       get() = _editMeterDataEvent

    private val observablePaidDate = dataSource.getLastObservablePaidDate()

    private val observableData: LiveData<List<MeterData>> = Transformations
        .switchMap(observablePaidDate) {
            updateMeterData(it)
    }

    private fun updateMeterData(paidDate: PaidDate?): LiveData<List<MeterData>> {
        return if (paidDate == null) {
            dataSource.getObservableMeterDataByDates()
        } else {
            dataSource.getObservableMeterDataByDates(paidDate.date)
        }
    }

    private val observablePrice = dataSource.getLastObservablePrice()
    private val observablePriceCount = dataSource.getObservablePriceCount()

    val calculator = MeterDataCalculator(observableData, observablePrice, observablePriceCount, reversed = true)

    val isPaidButtonVisible = Transformations.map(calculator.price) { price ->
        price > 0
    }

    val setPriceButtonVisible = observablePriceCount.map { count ->
        count == 0
    }

    private val _setPriceEvent = MutableLiveData<Event<Unit>>()
    val setPriceEvent: LiveData<Event<Unit>>
        get() = _setPriceEvent

    fun onPaid() {
        observableData.value?.let { data ->
            if(data.isNotEmpty()) {
                val lastDate = data.last().date
                val priceId = observablePrice.value?.id?:0
                val paidDate = PaidDate(date = lastDate, priceId = priceId)
                viewModelScope.launch {
                    dataSource.insertPaidDate(paidDate)
                }
            }
        }
    }

    fun onEditMeterData(id: Int) {
        _editMeterDataEvent.value = Event(id)
    }

    fun onAddMeterData() {
        _addMeterDataEvent.value = Event(Unit)
    }

    fun onSetPrice() {
        _setPriceEvent.value = Event(Unit)
    }
}