package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeterDataListViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: MeterDataListViewModel

    @Before
    fun initSubject(){
        DataHolder.clear()
        subject = MeterDataListViewModel()
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
        DataHolder.insert(MeterData(data1))
        DataHolder.insert(MeterData(data2))

        val dataToDisplay = subject.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data.toString(), `is`(data1.toString()))
        assertThat(dataToDisplay[1].data.toString(), `is`(data2.toString()))
    }
}