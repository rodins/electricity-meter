package com.sergeyrodin.electricitymeter.paidlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import com.sergeyrodin.electricitymeter.utils.dateToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

private val PRICE = Price(1, 1.68)

class PaidListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: PaidListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        dataSource.insertMeterDataBlocking(MeterData(14704))
        dataSource.insertPriceBlocking(PRICE)
        subject = PaidListViewModel(dataSource)
    }

    @Test
    fun noData_noDataIsTrue() {
        val noData = subject.noData.getOrAwaitValue()
        assertThat(noData, `is`(true))
    }

    @Test
    fun someData_noDataIsFalse() {
        val date = 1602219377796
        dataSource.insertPaidDateBlocking(PaidDate(date = date, priceId = PRICE.id))

        val noData = subject.noData.getOrAwaitValue()
        assertThat(noData, `is`(false))
    }

    @Test
    fun someData_dateEquals() {
        val date = 1602219377796
        dataSource.insertPaidDateBlocking(PaidDate(date = date, priceId = PRICE.id))

        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items[0].date, `is`(date))
    }

    @Test
    fun fewItems_sizeEquals() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        dataSource.insertPaidDateBlocking(PaidDate(date = date1, priceId = PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(date = date2, priceId = PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(date = date3, priceId = PRICE.id))

        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items.size, `is`(3))
    }

    @Test
    fun onItemClick_navigationEventIdEquals() {
        val id = 1
        subject.onItemClick(id)

        val event = subject.itemClickEvent.getOrAwaitValue()
        assertThat(event.getContentIfNotHandled(), `is`(id))
    }

    @Test
    fun onItemLongClick_highlightedPositionEquals() {
        val position = 1
        subject.onItemLongClick(position)

        val highlighted = subject.positionEvent.getOrAwaitValue()
        assertThat(highlighted, `is`(position))
    }

    @Test
    fun onItemClick_actionModeTrue() {
        val position = 1
        subject.onItemLongClick(position)

        val actionMode = subject.actionModeEvent.getOrAwaitValue()
        assertThat(actionMode, `is`(true))
    }

    @Test
    fun deleteSelectedPaidDate_sizeZero() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val position = 1
        dataSource.insertPaidDateBlocking(PaidDate(date = date1, priceId = PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(date = date2, priceId = PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(date = date3, priceId = PRICE.id))

        subject.onItemLongClick(position)
        subject.deleteSelectedPaidDate()

        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items.size, `is`(2))
        assertThat(items[0].date, `is`(date1))
        assertThat(items[1].date, `is`(date3))
    }

    @Test
    fun deletePaidDate_actionModeFalse() {
        val date1 = 1602219377796
        val position = 0
        dataSource.insertPaidDateBlocking(PaidDate(date = date1, priceId = PRICE.id))

        subject.onItemLongClick(position)
        subject.deleteSelectedPaidDate()

        val actionMode = subject.actionModeEvent.getOrAwaitValue()
        assertThat(actionMode, `is`(false))
    }

    @Test
    fun itemClick_actionModeEventIsFalse() {
        val id = 1
        val date = 1602219377796
        val position = 0
        val resetPosition = -1
        dataSource.insertPaidDateBlocking(PaidDate(id = id, date = date, priceId = PRICE.id))

        subject.onItemLongClick(position)
        subject.onItemClick(id)

        val actionMode = subject.actionModeEvent.getOrAwaitValue()
        assertThat(actionMode, `is`(false))
    }

    @Test
    fun actionMode_onHighlightedItemClick_navigationEventThrowsException() {
        val id = 1
        val date = 1602219377796
        val position = 0
        dataSource.insertPaidDateBlocking(PaidDate(id = id, date = date, priceId = PRICE.id))

        subject.onItemLongClick(position)
        subject.onItemClick(id)

        try{
            subject.itemClickEvent.getOrAwaitValue()
            fail()
        } catch(e: TimeoutException) {

        }
    }

    @Test
    fun actionMode_onNotHighlightedItemClick_navigationEventThrowsException() {
        val id1 = 1
        val id2 = 2
        val date1 = 1602219377796
        val date2 = 1604123777809
        val position = 0
        dataSource.insertPaidDateBlocking(PaidDate(id = id1, date = date1, priceId = PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(id = id2, date = date2, priceId = PRICE.id))

        subject.onItemLongClick(position)
        subject.onItemClick(id2)

        try{
            subject.itemClickEvent.getOrAwaitValue()
            fail()
        } catch(e: TimeoutException) {

        }
    }

    @Test
    fun testResetHighlightedPosition() {
        val id = 1
        val date = 1602219377796
        val position = 0
        dataSource.insertPaidDateBlocking(PaidDate(id = id, date = date, priceId = PRICE.id))

        subject.onItemLongClick(position)

        subject.onDestroyActionMode()

        val highlighted = subject.positionEvent.getOrAwaitValue()
        assertThat(highlighted, `is`(position))
    }

    @Test
    fun onePaidDate_totalPriceEquals() {
        val date1 = dateToLong(2020, 12, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, 12, 30, 9, 0)
        val data2 = 15123
        val totalPrice = 703.92
        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertPaidDateBlocking(PaidDate(1, date2, PRICE.id))

        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items[0].price, `is`(totalPrice))
    }

    @Test
    fun twoPaidDates_totalPriceEquals() {
        val date1 = dateToLong(2020, 12, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, 12, 30, 9, 0)
        val data2 = 15123
        val date3 = dateToLong(2021, 2, 1, 9, 0)
        val data3 = 15359
        val totalPrice1 = 703.92
        val totalPrice2 = 396.48
        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertPaidDateBlocking(PaidDate(1, date2, PRICE.id))
        dataSource.insertPaidDateBlocking(PaidDate(2, date3, PRICE.id))

        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items[0].price, `is`(totalPrice1))
        assertThat(items[1].price, `is`(totalPrice2))
    }

    @Test
    fun noPaidDates_itemsEmpty() {
        val items = subject.pricePaidDates.getOrAwaitValue()
        assertThat(items.isEmpty(), `is`(true))
    }
}