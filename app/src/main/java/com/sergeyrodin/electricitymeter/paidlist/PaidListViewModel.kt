package com.sergeyrodin.electricitymeter.paidlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import kotlinx.coroutines.launch

class PaidListViewModel @ViewModelInject constructor(
    private val dataSource: MeterDataSource,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _highlightedPosition = MutableLiveData(-1)
    val highlightedPosition: LiveData<Int>
        get() = _highlightedPosition

    val paidDates = dataSource.getPaidDates()

    val noData = Transformations.map(paidDates) {
        it.isNullOrEmpty()
    }

    private val _itemClickEvent = MutableLiveData<Event<Int>>()
    val itemClickEvent: LiveData<Event<Int>>
        get() = _itemClickEvent

    fun onItemClick(id: Int) {
        if (_highlightedPosition.value == -1) {
            _itemClickEvent.value = Event(id)
        } else {
            _highlightedPosition.value = -1
        }
    }

    fun onItemLongClick(position: Int) {
        _highlightedPosition.value = position
    }

    fun deleteSelectedPaidDate() {
        viewModelScope.launch {
            val position = _highlightedPosition.value
            position?.let {
                val paidDate = paidDates.value?.get(position)
                dataSource.deletePaidDate(paidDate)
                _highlightedPosition.value = -1
            }
        }
    }
}