package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
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
    private lateinit var subject: MeterDataListViewModel

    @Before
    fun initSubject(){
        dataSource = FakeDataSource()
    }

    @Test
    fun dataParam_dataToDisplayEquals() {
        val data1 = 14556
        val data2 = 14579
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        subject = MeterDataListViewModel(dataSource)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
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
        subject = MeterDataListViewModel(dataSource)
        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()

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
        subject = MeterDataListViewModel(dataSource)

        val totalValue = subject.total.getOrAwaitValue()
        assertThat(totalValue, `is`(total))
    }

    @Test
    fun noItems_totalZero() {
        subject = MeterDataListViewModel(dataSource)
        val totalValue = subject.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun oneItem_totalZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(dataSource)

        val totalValue = subject.total.getOrAwaitValue()
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
        subject = MeterDataListViewModel(dataSource)

        val avgValue = subject.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(avg))
    }

    @Test
    fun noItems_avgZero() {
        subject = MeterDataListViewModel(dataSource)
        val avgValue = subject.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(0))
    }

    @Test
    fun oneItem_avgZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(dataSource)

        val avgValue = subject.avg.getOrAwaitValue()
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
        subject = MeterDataListViewModel(dataSource)
        val price = 466.32

        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(price))
    }

    @Test
    fun fewItems_lessThenHundred_totalPriceEquals() {
        val data1 = 14594
        val data2 = 14611
        val data3 = 14622
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))
        subject = MeterDataListViewModel(dataSource)
        val price = 39.6

        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(price))
    }

    @Test
    fun noItems_priceZero() {
        subject = MeterDataListViewModel(dataSource)
        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_priceZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))
        subject = MeterDataListViewModel(dataSource)

        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun twoItems_lowPrice_dailyPriceEquals() {
        val data1 = 14622
        val data2 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        subject = MeterDataListViewModel(dataSource)
        val price1 = 0.0
        val price2 = 14.4

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()

        assertThat(dataToDisplay[0].price, `is`(price1))
        assertThat(dataToDisplay[1].price, `is`(price2))
    }

    @Test
    fun threeItems_highPrice3_dailyPriceEquals() {
        val data1 = 14622
        val data2 = 14722
        val data3 = 14723
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        subject = MeterDataListViewModel(dataSource)
        val price1 = 0.0
        val price2 = 90.0
        val price3 = 1.68

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].price, `is`(price1))
        assertThat(dataToDisplay[1].price, `is`(price2))
        assertThat(dataToDisplay[2].price, `is`(price3))
    }

    @Test
    fun threeItems_dayOnBorderSmallAndBig_dailyPriceEquals() {
        val data1 = 14622
        val data2 = 14692
        val data3 = 14762
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        subject = MeterDataListViewModel(dataSource)
        val price1 = 0.0
        val price2 = 63.0
        val price3 = 94.2

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].price, `is`(price1))
        assertThat(dataToDisplay[1].price, `is`(price2))
        assertThat(dataToDisplay[2].price, `is`(price3))
    }

    @Test
    fun oneItem_dailyPriceZero() {
        val data = 114622
        dataSource.testInsert(MeterData(data))
        subject = MeterDataListViewModel(dataSource)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].price, `is`(0.0))
    }

    @Test
    fun fewItems_pricesEqual() {
        val data1 = 14314
        val price1 = 0.0
        val data2 = 14322
        val price2 = 7.2
        val data3 = 14330
        val price3 = 7.2
        val data4 = 14340
        val price4 = 9.0
        val data5 = 14348
        val price5 = 7.2
        val data6 = 14359
        val price6 = 9.9
        val data7 = 14371
        val price7 = 10.8
        val data8 = 14386
        val price8 = 13.5
        val data9 = 14401
        val price9 = 13.5
        val data10 = 14415
        val price10 = 13.38
        val data11 = 14427
        val price11 = 20.16
        val data12 = 14443
        val price12 = 26.88
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))
        dataSource.testInsert(MeterData(data5))
        dataSource.testInsert(MeterData(data6))
        dataSource.testInsert(MeterData(data7))
        dataSource.testInsert(MeterData(data8))
        dataSource.testInsert(MeterData(data9))
        dataSource.testInsert(MeterData(data10))
        dataSource.testInsert(MeterData(data11))
        dataSource.testInsert(MeterData(data12))
        subject = MeterDataListViewModel(dataSource)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].price, `is`(price1))
        assertThat(dataToDisplay[1].price, `is`(price2))
        assertThat(dataToDisplay[2].price, `is`(price3))
        assertThat(dataToDisplay[3].price, `is`(price4))
        assertThat(dataToDisplay[4].price, `is`(price5))
        assertThat(dataToDisplay[5].price, `is`(price6))
        assertThat(dataToDisplay[6].price, `is`(price7))
        assertThat(dataToDisplay[7].price, `is`(price8))
        assertThat(dataToDisplay[8].price, `is`(price9))
        assertThat(dataToDisplay[9].price, `is`(price10))
        assertThat(dataToDisplay[10].price, `is`(price11))
        assertThat(dataToDisplay[11].price, `is`(price12))
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
        subject = MeterDataListViewModel(dataSource)

        subject.onPaid()

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
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
        subject = MeterDataListViewModel(dataSource)

        subject.onPaid()

        dataSource.testInsert(MeterData(data3))
        subject = MeterDataListViewModel(dataSource)

        subject.onPaid()

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
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
        subject = MeterDataListViewModel(dataSource)

        subject.onPaid()

        subject = MeterDataListViewModel(dataSource)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
    }

    @Test
    fun noItems_eventIsTrue() {
        subject = MeterDataListViewModel(dataSource)

        val noItems = subject.noItems.getOrAwaitValue()
        assertThat(noItems, `is`(true))
    }

    @Test
    fun oneItem_noItemsEventsFalse() {
        val data = 14509
        dataSource.testInsert(MeterData(data))
        subject = MeterDataListViewModel(dataSource)

        val noItems = subject.noItems.getOrAwaitValue()
        assertThat(noItems, `is`(false))
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

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))
        dataSource.testInsert(MeterData(data5))

        val paidDate1 = PaidDate(1, date2)
        val paidDate2 = PaidDate(2, date4)

        dataSource.testInsert(paidDate1)
        dataSource.testInsert(paidDate2)

        subject = MeterDataListViewModel(dataSource, paidDate1.id)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, `is`(3))
        assertThat(dataToDisplay[0].data, `is`(data2))
        assertThat(dataToDisplay[1].data, `is`(data3))
        assertThat(dataToDisplay[2].data, `is`(data4))
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

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))

        val paidDate1 = PaidDate(1, date2)
        val paidDate2 = PaidDate(2, date4)

        dataSource.testInsert(paidDate1)
        dataSource.testInsert(paidDate2)

        subject = MeterDataListViewModel(dataSource, paidDate2.id)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
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

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))

        val paidDate1 = PaidDate(1, date4)

        dataSource.testInsert(paidDate1)

        subject = MeterDataListViewModel(dataSource, paidDate1.id)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
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

        subject = MeterDataListViewModel(dataSource)

        subject.onPaid()

        dataSource.testInsert(MeterData(data5))
        subject = MeterDataListViewModel(dataSource)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, `is`(data4))
        assertThat(dataToDisplay[1].data, `is`(data5))
    }

    @Test
    fun onAddMeterDataFab_addMeterDataEventNotNull() {
        subject = MeterDataListViewModel(dataSource)

        subject.onAddMeterData()

        val event = subject.addMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }
}