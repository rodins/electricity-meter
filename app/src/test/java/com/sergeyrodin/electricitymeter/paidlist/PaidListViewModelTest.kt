package com.sergeyrodin.electricitymeter.paidlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PaidListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var subject: PaidListViewModel

    @Before
    fun init() {
        dataSource = FakeDataSource()
        subject = PaidListViewModel(dataSource)
    }

    @Test
    fun noData_noDataIsTrue() {
        val noData = subject.noData.getOrAwaitValue()
        assertThat(noData, `is`(true))
    }

    @Test
    fun someData_noDataIsFalse() {
        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val noData = subject.noData.getOrAwaitValue()
        assertThat(noData, `is`(false))
    }

    @Test
    fun someData_dateEquals() {
        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val items = subject.paidDates.getOrAwaitValue()
        assertThat(items[0].date, `is`(date))
    }

    @Test
    fun fewItems_sizeEquals() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        dataSource.testInsert(PaidDate(date = date3))

        val items = subject.paidDates.getOrAwaitValue()
        assertThat(items.size, `is`(3))
    }
}