package com.sergeyrodin.electricitymeter.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import com.sergeyrodin.electricitymeter.utils.MeterDataCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MeterDataHistoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: MeterDataHistoryViewModel

    @Before
    fun initSubject(){
        dataSource = FakeDataSource()
        val calculator = MeterDataCalculator(dataSource)
        subject = MeterDataHistoryViewModel(calculator, SavedStateHandle())
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

        subject.start(paidDate1.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay.size, CoreMatchers.`is`(3))
        assertThat(dataToDisplay[0].data, CoreMatchers.`is`(data2))
        assertThat(dataToDisplay[1].data, CoreMatchers.`is`(data3))
        assertThat(dataToDisplay[2].data, CoreMatchers.`is`(data4))
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

        subject.start(paidDate2.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, CoreMatchers.`is`(data4))
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

        subject.start(paidDate1.id)

        val dataToDisplay = subject.calculator.dataToDisplay.getOrAwaitValue()
        assertThat(dataToDisplay[0].data, CoreMatchers.`is`(data4))
    }
}