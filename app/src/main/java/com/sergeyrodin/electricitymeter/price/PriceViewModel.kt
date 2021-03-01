package com.sergeyrodin.electricitymeter.price

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

    private val observablePrice = dataSource.getObservablePrice()
    private val observablePriceCount = dataSource.getObservablePriceCount()

    val priceText: LiveData<String> = observablePriceCount.switchMap { count ->
        if(priceIsSet(count)) {
            getObservablePriceText()
        } else {
            observableEmptyString()
        }
    }

    private fun priceIsSet(count: Int) = count != 0

    private fun getObservablePriceText() = observablePrice.map {
        it.price.toString()
    }

    private fun observableEmptyString() = MutableLiveData("")

    fun onSaveFabClick(price: String) {
        viewModelScope.launch {
            try {
                dataSource.insertPrice(Price(1, price.toDouble()))
                _saveEvent.value = Event(Unit)
            } catch(e: NumberFormatException) {

            }
        }
    }

}