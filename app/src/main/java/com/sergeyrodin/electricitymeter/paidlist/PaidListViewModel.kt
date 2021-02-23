package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaidListViewModel @Inject constructor(
    private val dataSource: MeterDataSource
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
                resetHighlightedPosition()
            }
        }
    }

    fun resetHighlightedPosition() {
        _highlightedPosition.value = -1
    }
}