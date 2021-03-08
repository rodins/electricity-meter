package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import com.sergeyrodin.electricitymeter.utils.dateToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeoutException

private const val YEAR = 2021
private const val MONTH = Calendar.JANUARY

class MeterDataListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MeterDataListViewModel

    @Before
    fun initSubject(){
        dataSource = FakeDataSource()
        dataSource.insertPriceBlocking(Price(1, 1.68))
        subject = MeterDataListViewModel(dataSource)
    }

    @Test
    fun dataParam_dataToDisplayEquals() {
        val data1 = 14556
        val data2 = 14579
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data2))
        assertThat(dataToDisplay[1].data, `is`(data1))
    }

    @Test
    fun twoDataItems_diffEquals() {
        val data1 = 14622
        val data2 = 14638
        val diff1 = 0
        val diff2 = 16
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()

        assertThat(dataToDisplay[0].diff, `is`(diff2))
        assertThat(dataToDisplay[1].diff, `is`(diff1))
    }

    @Test
    fun fewItems_totalEquals() {
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        val total = 324
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(total))
    }

    @Test
    fun noItems_totalZero() {
        subject = MeterDataListViewModel(dataSource)
        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun oneItem_totalZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun fewItems_avgEquals() {
        val data1 = 14594
        val data2 = 14611
        val data3 = 14622
        val data4 = 14638
        val avg = 11
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val avgValue = subject.calculator.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(avg))
    }

    @Test
    fun noItems_avgZero() {
        subject = MeterDataListViewModel(dataSource)
        val avgValue = subject.calculator.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(0))
    }

    @Test
    fun oneItem_avgZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

        val avgValue = subject.calculator.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(0))
    }

    @Test
    fun fewItems_totalPriceEquals() {
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val price = 544.32

        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(price))
    }

    @Test
    fun noItems_priceZero() {
        subject = MeterDataListViewModel(dataSource)
        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_priceZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_dailyPriceZero() {
        val data = 114622
        dataSource.testInsert(MeterData(data))

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].price, `is`(0.0))
    }

    @Test
    fun onPaidData_lastItemEquals() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))

        val dataToDisplay1 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay1.size, `is`(4))

        subject.onPaid()

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
    }

    @Test
    fun onPaidData_addItems_dataEquals() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))

        val dataToDisplay1 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay1[0].data, `is`(data2))
        assertThat(dataToDisplay1.size, `is`(2))

        subject.onPaid()

        val dataToDisplay2 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay2[0].data, `is`(data2))
        assertThat(dataToDisplay2.size, `is`(1))

        dataSource.testInsert(MeterData(data3))

        val dataToDisplay3 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay3.size, `is`(2))

        subject.onPaid()

        val dataToDisplay4 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay4[0].data, `is`(data3))
    }

    @Test
    fun noItems_eventIsTrue() {
        val noItems = subject.calculator.noItems.getOrAwaitValue()
        assertThat(noItems, `is`(true))
    }

    @Test
    fun oneItem_noItemsEventsFalse() {
        val data = 14509
        dataSource.testInsert(MeterData(data))

        val noItems = subject.calculator.noItems.getOrAwaitValue()
        assertThat(noItems, `is`(false))
    }

    @Test
    fun addItemAfterOnPaid_dataEquals() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 15011

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))

        val dataToDisplay1 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay1.size, `is`(4))

        subject.onPaid()

        val dataToDisplay2 = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay2[0].data, `is`(data4))

        dataSource.testInsert(MeterData(data5))

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data5))
        assertThat(dataToDisplay[1].data, `is`(data4))
    }

    @Test
    fun onAddMeterData_addMeterDataEventNotNull() {
        subject.onAddMeterData()

        val event = subject.addMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun onEditMeterData_editMeterDataEventIdEquals() {
        val id = 1

        subject.onEditMeterData(id)

        val event = subject.editMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(id))
    }

    @Test
    fun priceNotZero_paidButtonVisibleIsTrue() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))

        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(true))
    }

    @Test
    fun priceZero_paidButtonVisibleIsFalse() {
        val data1 = 14314
        val date1 = 1602219377796
        dataSource.testInsert(MeterData(data1, date = date1))

        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(false))
    }

    @Test
    fun noItems_paidButtonVisibleIsFalse() {
        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(false))
    }

    @Test
    fun fewItems_prognosisEquals() {
        val data1 = 14594
        val date1 = dateToLong(YEAR, MONTH,19, 9, 0)

        val data2 = 14611
        val date2 = dateToLong(YEAR, MONTH,20, 8, 10)

        val data3 = 14622
        val date3 = dateToLong(YEAR, MONTH,22, 9, 5)

        val data4 = 14638
        val date4 = dateToLong(YEAR, MONTH,23, 8, 30)

        val prognosis = 577.92

        dataSource.testInsert(MeterData(data1, 1, date1))
        dataSource.testInsert(MeterData(data2, 2, date2))
        dataSource.testInsert(MeterData(data3, 3, date3))
        dataSource.testInsert(MeterData(data4, 4, date4))

        val prognosisValue = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosisValue, `is`(prognosis))
    }

    @Test
    fun prognosisNovemberEquals() {
        val data1 = 14314
        val date1 = dateToLong(2020, Calendar.NOVEMBER,1, 9, 0)

        val data2 = 14322
        val date2 = dateToLong(2020, Calendar.NOVEMBER,2, 9, 0)

        val prognosis = 403.2

        dataSource.testInsert(MeterData(data1, 1, date1))
        dataSource.testInsert(MeterData(data2, 2, date2))

        val prognosisValue = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosisValue, `is`(prognosis))
    }

    @Test
    fun prognosisDecemberEquals() {
        val data1 = 14735
        val date1 = dateToLong(2020, Calendar.DECEMBER,9, 9, 0)

        val data2 = 14757
        val date2 = dateToLong(2020, Calendar.DECEMBER,10, 9, 0)

        val prognosis = 1145.76

        dataSource.testInsert(MeterData(data1, 1, date1))
        dataSource.testInsert(MeterData(data2, 2, date2))

        val prognosisValue = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosisValue, `is`(prognosis))
    }

    @Test
    fun prognosisFebruaryEquals() {
        val data1 = 15359
        val date1 = dateToLong(2021, Calendar.FEBRUARY,1, 9, 0)

        val data2 = 15380
        val date2 = dateToLong(2021, Calendar.FEBRUARY,2, 9, 0)

        val prognosis = 987.84

        dataSource.testInsert(MeterData(data1, 1, date1))
        dataSource.testInsert(MeterData(data2, 2, date2))

        val prognosisValue = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosisValue, `is`(prognosis))
    }

    @Test
    fun prognosisLeapFebruaryEquals() {
        val data1 = 13476
        val date1 = dateToLong(2020, Calendar.FEBRUARY,2, 9, 0)

        val data2 = 13490
        val date2 = dateToLong(2020, Calendar.FEBRUARY,3, 9, 0)

        val prognosis = 682.08

        dataSource.testInsert(MeterData(data1, 1, date1))
        dataSource.testInsert(MeterData(data2, 2, date2))

        val prognosisValue = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosisValue, `is`(prognosis))
    }

    @Test
    fun priceChanged_totalPriceEquals() {
        val price1 = 1.68
        val price2 = 2.0
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))
        dataSource.insertPriceBlocking(Price(1, price1))

        val totalPrice1 = 544.32
        val totalPrice2 = 648.0

        val priceValue1 = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue1, `is`(totalPrice1))

        dataSource.insertPriceBlocking(Price(1, price2))

        val priceValue2 = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue2, `is`(totalPrice2))
    }

    @Test
    fun noPriceSet_totalPriceZero() {
        dataSource.deletePriceBlocking()

        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val totalPrice = subject.calculator.price.getOrAwaitValue()
        assertThat(totalPrice, `is`(0.0))
    }

    @Test
    fun noPriceSet_prognosisZero() {
        dataSource.deletePriceBlocking()

        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val prognosis = subject.calculator.prognosis.getOrAwaitValue()
        assertThat(prognosis, `is`(0.0))
    }

    @Test
    fun noPriceSet_dataToDisplaySizeZero() {
        dataSource.deletePriceBlocking()

        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(0))
    }

    @Test
    fun noPriceSet_setPriceButtonVisibleTrue() {
        dataSource.deletePriceBlocking()

        val setPrice = subject.setPriceButtonVisible.getOrAwaitValue()
        assertThat(setPrice, `is`(true))
    }

    @Test
    fun priceSet_setPriceButtonVisibleFalse() {
        dataSource.insertPriceBlocking(Price(1, 1.68))

        val setPrice = subject.setPriceButtonVisible.getOrAwaitValue()
        assertThat(setPrice, `is`(false))
    }

    @Test
    fun onSetPriceButton_setPriceEventNotNull() {
        subject.onSetPrice()

        val event = subject.setPriceEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

}