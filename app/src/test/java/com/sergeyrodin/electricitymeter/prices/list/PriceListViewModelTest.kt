package com.sergeyrodin.electricitymeter.prices.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PriceListViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: PriceListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = PriceListViewModel(dataSource)
    }

    @Test
    fun onAddPrice_addPriceEventNotNull() {
        subject.onAddPrice()

        val event = subject.addPriceEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun insertPrice_priceEquals() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        val prices = subject.prices.getOrAwaitValue()

        assertThat(prices[0].price, `is`(price.price))
    }

    @Test
    fun insertTwoPrices_pricesEqual() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        val prices = subject.prices.getOrAwaitValue()

        assertThat(prices.size, `is`(2))
    }

    @Test
    fun insertPrice_deletePrice_priceCountZero() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)

        subject.onDeletePrice()

        val count = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(count, `is`(0))
    }

    @Test
    fun twoPrices_deletePrice_priceCountEquals() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        subject.onPriceLongClick(price2)

        subject.onDeletePrice()

        val count = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(count, `is`(1))
    }

    @Test
    fun twoPrices_deletePrice_actionDeleteEventFalse() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        subject.onPriceLongClick(price2)

        subject.onDeletePrice()

        val visible = subject.actionDeleteEvent.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun insertPrice_priceLongPress_actionDeleteEventIsTrue() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)

        val visible = subject.actionDeleteEvent.getOrAwaitValue()
        assertThat(visible, `is`(true))
    }

    @Test
    fun priceLongClick_selectedPriceEventEquals() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)

        val selectedPrice = subject.selectedPriceEvent.getOrAwaitValue()
        assertThat(selectedPrice?.price, `is`(price.price))
    }

    @Test
    fun highlightedPriceClick_selectedPriceEventNull() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)
        subject.onPriceClick()

        val selectedPrice = subject.selectedPriceEvent.getOrAwaitValue()
        assertThat(selectedPrice, `is`(nullValue()))
    }

    @Test
    fun highlightedPriceClick_actionDeleteEventFalse() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)
        subject.onPriceClick()

        val visible = subject.actionDeleteEvent.getOrAwaitValue()
        assertThat(visible, `is`(false))
    }

    @Test
    fun onDestroyActionMode_selectedPriceEventNull() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        subject.onPriceLongClick(price)
        subject.onDestroyActionMode()

        val selectedPrice = subject.selectedPriceEvent.getOrAwaitValue()
        assertThat(selectedPrice, `is`(nullValue()))
    }

    @Test
    fun noPrices_noPricesEventTrue() {
        val noPrices = subject.noPricesEvent.getOrAwaitValue()
        assertThat(noPrices, `is`(true))
    }

    @Test
    fun priceSet_noPricesEventFalse() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        val noPrices = subject.noPricesEvent.getOrAwaitValue()
        assertThat(noPrices, `is`(false))
    }

    @Test
    fun deletePriceWithPaidDateConstraint_priceNotDeleted() {
        val price = Price(1, 1.68)
        val paidDate = PaidDate(1, System.currentTimeMillis(), price.id)
        dataSource.insertPriceBlocking(price)
        dataSource.insertPaidDateBlocking(paidDate)

        subject.onPriceLongClick(price)
        subject.onDeletePrice()

        val count = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(count, `is`(1))
    }

    @Test
    fun deletePrice_deleteErrorEventEquals() {
        val price = Price(1, 1.68)
        val paidDate = PaidDate(1, System.currentTimeMillis(), price.id)
        dataSource.insertPriceBlocking(price)
        dataSource.insertPaidDateBlocking(paidDate)

        subject.onPriceLongClick(price)
        subject.onDeletePrice()

        val event = subject.deleteErrorEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(1))
    }

    @Test
    fun migratedPaidDatePriceIdZero_deletePrice_deleteErrorEventEquals() {
        val price = Price(1, 1.68)
        val paidDate = PaidDate(1, System.currentTimeMillis(), 0)
        dataSource.insertPriceBlocking(price)
        dataSource.insertPaidDateBlockingMigrated(paidDate)

        subject.onPriceLongClick(price)
        subject.onDeletePrice()

        val event = subject.deleteErrorEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(1))
    }
}