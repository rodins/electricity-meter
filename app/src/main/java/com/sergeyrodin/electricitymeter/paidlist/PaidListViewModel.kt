package com.sergeyrodin.electricitymeter.paidlist

import androidx.lifecycle.*
import com.sergeyrodin.electricitymeter.Event
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.utils.calculatePrice
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

    private val paidDates = dataSource.getPaidDates()

    val pricePaidDates = paidDates.switchMap { items ->
        liveData {
            val pricePaidDateItems = getPricePaidDates(items)
            emit(pricePaidDateItems)
        }
    }

    private suspend fun getPricePaidDates(
        items: List<PaidDate>
    ): List<PricePaidDate> {
        var startDate = 0L
        return items.map { paidDate ->

            val totalPrice = getTotalPriceByPaidDate(startDate, paidDate)

            startDate = paidDate.date

            PricePaidDate(paidDate.id, paidDate.date, totalPrice)
        }
    }

    private suspend fun getTotalPriceByPaidDate(
        startDate: Long,
        paidDate: PaidDate
    ): Double {
        val kwhPrice = getKwhPrice()

        val startData = getStartData(startDate)

        val endData = getEndData(paidDate)

        return getTotalPrice(endData, startData, kwhPrice)
    }

    private suspend fun getKwhPrice(): Double {
        val price = dataSource.getPrice()
        return price?.price ?: 0.0
    }

    private suspend fun getStartData(
        startDate: Long
    ): Int {
        val startMeterData = getStartMeterData(startDate)
        return startMeterData?.data ?: 0
    }

    private suspend fun getStartMeterData(startDate: Long) = if (startDate == 0L) {
        dataSource.getFirstMeterData()
    } else {
        dataSource.getMeterDataByDate(startDate)
    }

    private suspend fun getEndData(paidDate: PaidDate): Int {
        val endDate = getEndDate(paidDate)
        val endMeterData = getEndMeterData(endDate)
        return endMeterData?.data ?: 0
    }

    private fun getEndDate(paidDate: PaidDate) = paidDate.date

    private suspend fun getEndMeterData(endDate: Long) = dataSource.getMeterDataByDate(endDate)

    private fun getTotalPrice(
        endData: Int,
        startData: Int,
        kwhPrice: Double
    ): Double {
        val totalKwh = calculateTotalKwh(endData, startData)
        return calculatePrice(totalKwh, kwhPrice)
    }

    private fun calculateTotalKwh(endData: Int, startData: Int) = endData - startData

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