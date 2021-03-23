package com.sergeyrodin.electricitymeter.history

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

private val PRICE = Price(1, 1.68)

class MeterDataHistoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MeterDataHistoryViewModel

    @Before
    fun initSubject() {
        dataSource = FakeDataSource()
        dataSource.insertPriceBlocking(PRICE)
        subject = MeterDataHistoryViewModel(dataSource)
    }

    @Test
    fun displayPaidDatesIntervalByInputId() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertMeterDataBlocking(MeterData(data4, date = date4))
        dataSource.insertMeterDataBlocking(MeterData(data5))

        val paidDate1 = PaidDate(1, date2, PRICE.id)
        val paidDate2 = PaidDate(2, date4, PRICE.id)

        dataSource.insertPaidDateBlocking(paidDate1)
        dataSource.insertPaidDateBlocking(paidDate2)

        subject.start(paidDate1.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(2))
        assertThat(dataToDisplay[0].data, `is`(data1))
        assertThat(dataToDisplay[1].data, `is`(data2))
    }

    @Test
    fun displayPaidDatesIntervalByInputIdLastElement() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertMeterDataBlocking(MeterData(data4, date = date4))

        val paidDate1 = PaidDate(1, date2, PRICE.id)
        val paidDate2 = PaidDate(2, date4, PRICE.id)

        dataSource.insertPaidDateBlocking(paidDate1)
        dataSource.insertPaidDateBlocking(paidDate2)

        subject.start(paidDate2.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(3))
        assertThat(dataToDisplay[0].data, `is`(data2))
        assertThat(dataToDisplay[1].data, `is`(data3))
        assertThat(dataToDisplay[2].data, `is`(data4))
    }

    @Test
    fun onePaidDate_displayPaidDatesIntervalByInputIdLastElement() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertMeterDataBlocking(MeterData(data4, date = date4))

        val paidDate1 = PaidDate(1, date4, PRICE.id)

        dataSource.insertPaidDateBlocking(paidDate1)

        subject.start(paidDate1.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(4))
        assertThat(dataToDisplay[0].data, `is`(data1))
        assertThat(dataToDisplay[3].data, `is`(data4))
    }

    @Test
    fun priceUpdated_oldPriceUsedForHistoryList_totalPriceEquals() {
        dataSource.insertPriceBlocking(Price(2, 2.0))

        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        val historyTotalPrice = 16.8

        val paidDate = PaidDate(1, date2, PRICE.id)

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertPaidDateBlocking(paidDate)

        subject.start(paidDate.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(2))

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(historyTotalPrice))
    }

    @Test
    fun paidDateAfterMigration_totalPriceEquals() {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        val historyTotalPrice = 16.8

        val paidDate = PaidDate(1, date2, PRICE.id)

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertPaidDateBlocking(paidDate)

        subject.start(paidDate.id)

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(historyTotalPrice))
    }

    @Test
    fun threePrices_paidDatePriceIdSecondPrice_historyTotalPriceEquals() {
        val price2 = Price(2, 2.0)
        val price3 = Price(3, 3.0)
        dataSource.insertPriceBlocking(price2)
        dataSource.insertPriceBlocking(price3)

        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        val historyTotalPrice = 20.0

        val paidDate = PaidDate(1, date2, price2.id)

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertPaidDateBlocking(paidDate)

        subject.start(paidDate.id)

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(historyTotalPrice))
    }

    @Test
    fun twoPaidDatesThreePrices_firstPaidDateId_historyTotalPriceEquals() {
        val price2 = Price(2, 2.0)
        val price3 = Price(3, 3.0)
        dataSource.insertPriceBlocking(price2)
        dataSource.insertPriceBlocking(price3)

        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714
        val date3 = dateToLong(2020, Calendar.DECEMBER, 5, 9, 0)
        val data3 = 14724

        val historyTotalPrice = 20.0

        val paidDate1 = PaidDate(1, date2, price2.id)
        val paidDate2 = PaidDate(2, date3, price3.id)

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertPaidDateBlocking(paidDate1)
        dataSource.insertPaidDateBlocking(paidDate2)

        subject.start(paidDate1.id)

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(historyTotalPrice))
    }

    @Test
    fun twoPaidDatesThreePrices_secondPaidDateId_historyTotalPriceEquals() {
        val price2 = Price(2, 2.0)
        val price3 = Price(3, 3.0)
        dataSource.insertPriceBlocking(price2)
        dataSource.insertPriceBlocking(price3)

        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714
        val date3 = dateToLong(2020, Calendar.DECEMBER, 5, 9, 0)
        val data3 = 14724

        val historyTotalPrice = 30.0

        val paidDate1 = PaidDate(1, date2, price2.id)
        val paidDate2 = PaidDate(2, date3, price3.id)

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertPaidDateBlocking(paidDate1)
        dataSource.insertPaidDateBlocking(paidDate2)

        subject.start(paidDate2.id)

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(historyTotalPrice))
    }
}