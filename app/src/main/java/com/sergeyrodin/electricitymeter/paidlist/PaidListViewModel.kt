package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource

class PaidListViewModel(dataSource: MeterDataSource) : ViewModel() {

    val paidDates = dataSource.getPaidDates()

    val noData = Transformations.map(paidDates) {
        it.isNullOrEmpty()
    }

    private val _itemClickEvent = MutableLiveData<Event<Int>>()
    val itemClickEvent: LiveData<Event<Int>>
        get() = _itemClickEvent

    fun onItemClick(id: Int) {
        _itemClickEvent.value = Event(id)
    }
}