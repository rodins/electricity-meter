package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditMeterDataViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: EditMeterDataViewModel

    @Before
    fun initViewModel() {
        dataSource = FakeDataSource()
        subject = EditMeterDataViewModel(dataSource, SavedStateHandle())
    }

    @Test
    fun meterDataIdArgument_dataEquals() {
        val id = 1
        val data = 14509
        dataSource.testInsert(MeterData(id = id, data = data))
        subject.start(id)

        val dataToDisplay = subject.data.getOrAwaitValue()
        assertThat(dataToDisplay, `is`(data.toString()))
    }

    @Test
    fun meterDataUpdated() {
        val id = 1
        val data = 14509
        val newData = 14511
        dataSource.testInsert(MeterData(id = id, data = data))
        subject.start(id)

        subject.onSaveMeterData(newData.toString())

        val items = dataSource.getMeterDataForTest()
        assertThat(items.size, `is`(1))
        assertThat(items[0].data, `is`(newData))
    }

    @Test
    fun fewMeterDataItems_meterDataUpdated() {
        val id1 = 1
        val data1 = 14314
        val date1 = 1602219377796
        val id2 = 2
        val data2 = 14509
        val date2 = 1604123777809
        val id3 = 3
        val data3 = 14579
        val date3 = 1606715777809
        val id4 = 4
        val data4 = 14638
        val date4 = 1606802177809
        val newData = 14511
        dataSource.testInsert(MeterData(data1, id1, date1))
        dataSource.testInsert(MeterData(data2, id2, date2))
        dataSource.testInsert(MeterData(data3, id3, date3))
        dataSource.testInsert(MeterData(data4, id4, date4))
        subject.start(id2)

        subject.onSaveMeterData(newData.toString())
        val items = dataSource.getMeterDataForTest()
        assertThat(items[1].data, `is`(newData))
    }

    @Test
    fun onDeleteMeterData_saveMeterDataEventNotNull() {
        val id1 = 1
        val data1 = 14314
        dataSource.testInsert(MeterData(data1, id1))
        subject.start(id1)

        subject.onDeleteMeterData()

        val event = subject.saveMeterDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun deleteMeterData_itemDeleted() {
        val id1 = 1
        val data1 = 14314
        val date1 = 1602219377796
        val id2 = 2
        val data2 = 14509
        val date2 = 1604123777809
        val id3 = 3
        val data3 = 14579
        val date3 = 1606715777809
        val id4 = 4
        val data4 = 14638
        val date4 = 1606802177809
        dataSource.testInsert(MeterData(data1, id1, date1))
        dataSource.testInsert(MeterData(data2, id2, date2))
        dataSource.testInsert(MeterData(data3, id3, date3))
        dataSource.testInsert(MeterData(data4, id4, date4))
        subject.start(id2)

        subject.onDeleteMeterData()

        val items = dataSource.getMeterDataForTest()
        assertThat(items.size, `is`(3))
        assertThat(items[0].data, `is`(data1))
        assertThat(items[1].data, `is`(data3))
        assertThat(items[2].data, `is`(data4))
    }
}