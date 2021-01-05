package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddMeterDataViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: AddMeterDataViewModel

    @Before
    fun initViewModel() {
        dataSource = FakeDataSource()
        subject = AddMeterDataViewModel(dataSource)
    }

    @Test
    fun onSaveMeterData_saveMeterDataEventNotNull() {
        val data = 15075
        subject.onSaveMeterData(data.toString())

        val event = subject.saveMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun onSaveMeterData_meterDataEquals() {
        val data = 15075
        subject.onSaveMeterData(data.toString())

        val items = dataSource.getMeterDataForTest()
        assertThat(items[0].data, `is`(data))
    }

    @Test
    fun onSaveEmptyData_meterDataSizeZero() {
        subject.onSaveMeterData("")

        val items = dataSource.getMeterDataForTest()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun onSaveTextData_meterDataSizeZero() {
        subject.onSaveMeterData("text")

        val items = dataSource.getMeterDataForTest()
        Assert.assertThat(items.size, `is`(0))
    }

    @Test
    fun filterLowerValue() {
        val data1 = 14509
        val data2 = 14314
        dataSource.testInsert(MeterData(data1))

        subject.onSaveMeterData(data2.toString())

        val items = dataSource.getMeterDataForTest()
        assertThat(items.size, `is`(1))
        assertThat(items[0].data, `is`(data1))
    }

    @Test
    fun filterEqualValue() {
        val data = 14509
        dataSource.testInsert(MeterData(data))

        subject.onSaveMeterData(data.toString())

        val items = dataSource.getMeterDataForTest()
        assertThat(items.size, `is`(1))
    }
}
