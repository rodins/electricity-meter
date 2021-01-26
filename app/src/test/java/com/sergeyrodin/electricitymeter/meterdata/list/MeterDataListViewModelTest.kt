package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeterDataListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var calculator: MeterDataCalculator
    private lateinit var subject: MeterDataListViewModel

    @Before
    fun initSubject(){
        dataSource = FakeDataSource()
        calculator = MeterDataCalculator(dataSource)
    }

    @Test
    fun dataParam_dataToDisplayEquals() {
        val data1 = 14556
        val data2 = 14579
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data1))
        assertThat(dataToDisplay[1].data, `is`(data2))
    }

    @Test
    fun twoDataItems_diffEquals() {
        val data1 = 14622
        val data2 = 14638
        val diff1 = 0
        val diff2 = 16
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())
        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()

        assertThat(dataToDisplay[0].diff, `is`(diff1))
        assertThat(dataToDisplay[1].diff, `is`(diff2))
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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(total))
    }

    @Test
    fun noItems_totalZero() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())
        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun oneItem_totalZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val totalValue = subject.calculator.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun fewItems_avgEquals() {
        val data1 = 14594
        val data2 = 14611
        val data3 = 14622
        val data4 = 14638
        val avg = 14
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val avgValue = subject.calculator.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(avg))
    }

    @Test
    fun noItems_avgZero() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())
        val avgValue = subject.calculator.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(0))
    }

    @Test
    fun oneItem_avgZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())
        val price = 544.32

        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(price))
    }

    @Test
    fun noItems_priceZero() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())
        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_priceZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val priceValue = subject.calculator.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_dailyPriceZero() {
        val data = 114622
        dataSource.testInsert(MeterData(data))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        subject.onPaid()

        dataSource.testInsert(MeterData(data3))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        subject.onPaid()

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data3))
    }

    @Test
    fun paidDataBeginPointSaved() {
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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        subject.onPaid()

        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
    }

    @Test
    fun noItems_eventIsTrue() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val noItems = subject.calculator.noItems.getOrAwaitValue()
        assertThat(noItems, `is`(true))
    }

    @Test
    fun oneItem_noItemsEventsFalse() {
        val data = 14509
        dataSource.testInsert(MeterData(data))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

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

        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        subject.onPaid()

        dataSource.testInsert(MeterData(data5))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
        assertThat(dataToDisplay[1].data, `is`(data5))
    }

    @Test
    fun onAddMeterData_addMeterDataEventNotNull() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        subject.onAddMeterData()

        val event = subject.addMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun onEditMeterData_editMeterDataEventIdEquals() {
        val id = 1
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

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
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(true))
    }

    @Test
    fun priceZero_paidButtonVisibleIsFalse() {
        val data1 = 14314
        val date1 = 1602219377796
        dataSource.testInsert(MeterData(data1, date = date1))
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(false))
    }

    @Test
    fun noItems_paidButtonVisibleIsFalse() {
        subject = MeterDataListViewModel(calculator, SavedStateHandle())

        val paidVisible = subject.isPaidButtonVisible.getOrAwaitValue()
        assertThat(paidVisible, `is`(false))
    }
}