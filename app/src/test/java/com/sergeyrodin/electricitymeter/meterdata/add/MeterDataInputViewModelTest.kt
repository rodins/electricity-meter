package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.meterdata.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeterDataInputViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: MeterDataInputViewModel

    @Before
    fun initSubject() {
        subject = MeterDataInputViewModel()
    }

    @Test
    fun onSaveData_saveDataEventNotNull() {
        val data = "14579"
        subject.onSaveData(data)

        val event = subject.saveDataEvent.getOrAwaitValue().getContentIfNotHandled()
        assertThat(event, `is`(not(nullValue())))
    }

    @Test
    fun onSaveData_dataEquals() {
        val data = "14579"
        subject.onSaveData(data)

        val saved = DataHolder.data
        assertThat(saved[0].data.toString(), `is`(data))
    }

    @Test
    fun onSaveEmptyData_dataIsEmpty() {
        subject.onSaveData("")

        val saved = DataHolder.data
        assertThat(saved.isEmpty(), `is`(true))
    }
}