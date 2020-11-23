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
        assertThat(dataToDisplay[0].data.toString(), `is`(data1.toString()))
        assertThat(dataToDisplay[1].data.toString(), `is`(data2.toString()))
    }
}