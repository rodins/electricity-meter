package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import kotlinx.coroutines.launch

class MeterDataListViewModel @ViewModelInject constructor(
    private val dataSource: MeterDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addMeterDataEvent = MutableLiveData<Event<Unit>>()
    val addMeterDataEvent: LiveData<Event<Unit>>
        get() = _addMeterDataEvent

    private val _editMeterDataEvent = MutableLiveData<Event<Int>>()
    val editMeterDataEvent: LiveData<Event<Int>>
       get() = _editMeterDataEvent

    private val observablePaidDate = dataSource.getLastPaidDate()

    private val observableData: LiveData<List<MeterData>> = Transformations
        .switchMap(observablePaidDate) {
            updateMeterData(it)
    }

    private fun updateMeterData(paidDate: PaidDate?): LiveData<List<MeterData>> {
        return if (paidDate == null) {
            dataSource.getObservableData()
        } else {
            dataSource.getObservableData(paidDate.date)
        }
    }

    val calculator = MeterDataCalculator(observableData, reversed = true)

    val isPaidButtonVisible = Transformations.map(calculator.price) { price ->
        price > 0
    }

    fun onPaid() {
        observableData.value?.let { data ->
            if(data.isNotEmpty()) {
                val last = data.last()
                val paidDate = PaidDate(date = last.date)
                viewModelScope.launch{
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
}