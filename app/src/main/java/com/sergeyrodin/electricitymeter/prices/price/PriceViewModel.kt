package com.sergeyrodin.electricitymeter.prices.price

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class PriceViewModel @Inject constructor(private val dataSource: MeterDataSource) : ViewModel() {

    private val _saveEvent = MutableLiveData<Event<Unit>>()
    val saveEvent: LiveData<Event<Unit>>
       get() = _saveEvent

    fun onSaveFabClick(price: String) {
        viewModelScope.launch {
            try {
                dataSource.insertPrice(Price(price = price.toDouble()))
                _saveEvent.value = Event(Unit)
            } catch(e: NumberFormatException) {

            }
        }
    }

}