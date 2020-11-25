package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.FakeDataSource
import com.sergeyrodin.electricitymeter.meterdata.MainCoroutineRule
import com.sergeyrodin.electricitymeter.meterdata.getOrAwaitValue
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
        subject = MeterDataListViewModel(dataSource)
    }

    @Test
    fun onAddData_dataEquals() {
        val input = "14594"
        subject.onAddData(input)

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data.toString(), `is`(input))
    }

    @Test
    fun dataParam_dataToDisplayEquals() {
        val data1 = 14556
        val data2 = 14579
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))

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

        val totalValue = subject.total.getOrAwaitValue()
        assertThat(totalValue, `is`(total))
    }

    @Test
    fun noItems_totalZero() {
        val totalValue = subject.total.getOrAwaitValue()
        assertThat(totalValue, `is`(0))
    }

    @Test
    fun oneItem_totalZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

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

        val avgValue = subject.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(avg))
    }

    @Test
    fun noItems_avgZero() {
        val avgValue = subject.avg.getOrAwaitValue()
        assertThat(avgValue, `is`(0))
    }

    @Test
    fun oneItem_avgZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

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
        val price = 39.6

        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(price))
    }

    @Test
    fun noItems_priceZero() {
        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

    @Test
    fun oneItem_priceZero() {
        val data1 = 14638
        dataSource.testInsert(MeterData(data1))

        val priceValue = subject.price.getOrAwaitValue()
        assertThat(priceValue, `is`(0.0))
    }

}