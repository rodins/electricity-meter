package com.sergeyrodin.electricitymeter.prices.list

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceListViewModel @Inject constructor(private val dataSource: MeterDataSource) : ViewModel() {

    private val _deleteErrorEvent = MutableLiveData<Event<Int>>()
    val deleteErrorEvent: LiveData<Event<Int>>
         get() = _deleteErrorEvent

    private val _selectedPriceEvent = MutableLiveData<Price?>()
    val selectedPriceEvent: LiveData<Price?>
        get() = _selectedPriceEvent

    private val _actionDeleteEvent = MutableLiveData<Boolean>()
    val actionDeleteEvent: LiveData<Boolean>
        get() = _actionDeleteEvent

    val prices = dataSource.getObservablePrices()

    private val _addPriceEvent = MutableLiveData<Event<Unit>>()
    val addPriceEvent: LiveData<Event<Unit>>
        get() = _addPriceEvent

    val noPricesEvent = prices.map {
        it.isEmpty()
    }

    fun onAddPrice() {
        _addPriceEvent.value = Event(Unit)
    }

    fun onDeletePrice() {
        selectedPriceEvent.value?.let { price ->
            viewModelScope.launch {
                val count = dataSource.getPaidDatesCountByPriceId(price.id)
                if(count == 0) {
                    if(isPriceNotUsedByMigratedPaidDatesCallsDeleteErrorEvent(price)) {
                        dataSource.deletePrice(price)
                    }
                }else {
                    _deleteErrorEvent.value = Event(count)
                }
                _actionDeleteEvent.value = false
            }
        }
    }

    private suspend fun isPriceNotUsedByMigratedPaidDatesCallsDeleteErrorEvent(price: Price): Boolean {
        val migratedPaidDatesCount = dataSource.getPaidDatesCountByPriceId(0)
        val firstPrice = prices.value?.get(0)
        if(migratedPaidDatesCount > 0 && price == firstPrice) {
            _deleteErrorEvent.value = Event(migratedPaidDatesCount)
            return false
        }
        return true
    }

    fun onPriceLongClick(price: Price) {
        _selectedPriceEvent.value = price
        _actionDeleteEvent.value = true
    }

    fun onPriceClick() {
        _selectedPriceEvent.value = null
        _actionDeleteEvent.value = false
    }

    fun onDestroyActionMode() {
        _selectedPriceEvent.value = null
    }
}