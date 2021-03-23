package com.sergeyrodin.electricitymeter.prices.price

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

class PriceViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: PriceViewModel

    @Before
    fun createSubject() {
        dataSource = FakeDataSource()
        subject = PriceViewModel(dataSource)
    }

    @Test
    fun onSaveFabClicked_eventNotNull() {
        subject.onSaveFabClick("1.68")

        val event = subject.saveEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun onSaveFabClicked_priceSaved() {
        val price = 1.68
        subject.onSaveFabClick(price.toString())

        val priceFromDb = dataSource.getFirstPriceBlocking()
        assertThat(priceFromDb?.price, `is`(price))
    }

    @Test
    fun onSaveFabClick_emptyInput_priceNotSaved() {
        subject.onSaveFabClick("")

        val priceFromDb = dataSource.getFirstPriceBlocking()
        assertThat(priceFromDb?.price, `is`(nullValue()))
    }

    @Test
    fun onSaveFabClick_emptyInput_navigationEventNotCalled() {
        subject.onSaveFabClick("")

        try {
            subject.saveEvent.getOrAwaitValue().getContentIfNotHandled()
            fail()
        }catch (e: TimeoutException) {

        }
    }

    @Test
    fun insertPrice_saveNewPrice_priceEquals() {
        val price1 = 1.68
        val price2 = 2.0
        dataSource.insertPriceBlocking(Price(1, price1))

        subject.onSaveFabClick(price2.toString())

        val priceFromDb = dataSource.getLastPriceBlocking()
        assertThat(priceFromDb?.price, `is`(price2))
    }
}