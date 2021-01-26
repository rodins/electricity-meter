package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import kotlinx.coroutines.launch

class MeterDataListViewModel @ViewModelInject constructor(
    val calculator: MeterDataCalculator,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addMeterDataEvent = MutableLiveData<Event<Unit>>()
    val addMeterDataEvent: LiveData<Event<Unit>>
        get() = _addMeterDataEvent

    private val _editMeterDataEvent = MutableLiveData<Event<Int>>()
    val editMeterDataEvent: LiveData<Event<Int>>
       get() = _editMeterDataEvent

    val isPaidButtonVisible = Transformations.map(calculator.price) { price ->
        price > 0
    }

    init {
        viewModelScope.launch {
            updateMeterData()
        }
    }

    private suspend fun updateMeterData() {
        val paidDate = calculator.dataSource.getLastPaidDate()
        if (paidDate == null) {
            calculator.updateObservableData()
        } else {
            calculator.updateObservableData(paidDate.date)
        }
    }

    fun onPaid() {
        calculator.observableData.value?.let { data ->
            if(data.isNotEmpty()) {
                val last = data.last()
                val paidDate = PaidDate(date = last.date)
                viewModelScope.launch{
                    calculator.dataSource.insertPaidDate(paidDate)
                    calculator.updateObservableData(last.date)
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