package com.sergeyrodin.electricitymeter.price

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

class PriceViewModelTest {

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

        val priceFromDb = dataSource.getPriceBlocking()
        assertThat(priceFromDb?.price, `is`(price))
    }

    @Test
    fun onSaveFabClick_emptyInput_priceNotSaved() {
        subject.onSaveFabClick("")

        val priceFromDb = dataSource.getPriceBlocking()
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
    fun priceSet_priceTextEquals() {
        val price = 1.68
        dataSource.insertPriceBlocking(Price(1, price))

        val priceText = subject.priceText.getOrAwaitValue()
        assertThat(priceText, `is`(price.toString()))
    }

    @Test
    fun noPriceSet_priceTextEmpty() {
        dataSource.insertPriceBlocking(Price(1, 1.68))
        dataSource.deletePriceBlocking()

        val priceText = subject.priceText.getOrAwaitValue()
        assertThat(priceText, `is`(""))
    }

    @Test
    fun priceSaved_priceTextEquals() {
        val price = 1.68
        subject.onSaveFabClick(price.toString())

        val priceText = subject.priceText.getOrAwaitValue()
        assertThat(priceText, `is`(price.toString()))
    }
}