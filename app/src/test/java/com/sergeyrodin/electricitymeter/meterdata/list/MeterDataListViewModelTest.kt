package com.sergeyrodin.electricitymeter.meterdata.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
    private val data = listOf(MeterData(14556), MeterData(14579))

    @Before
    fun initSubject(){
        subject = MeterDataListViewModel(data)
    }

    @Test
    fun onAddData_addDataEventNotNull() {
        subject.onAddData()

        val event = subject.addDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun dataParam_dataToDisplayEquals() {
        assertThat(subject.dataToDisplay[0], `is`(data[0]))
        assertThat(subject.dataToDisplay[1], `is`(data[1]))
    }
}