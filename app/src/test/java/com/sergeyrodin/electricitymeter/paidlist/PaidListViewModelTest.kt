package com.sergeyrodin.electricitymeter.paidlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.MainCoroutineRule
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

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
        subject = PaidListViewModel(dataSource, SavedStateHandle())
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

    @Test
    fun onItemClick_navigationEventIdEquals() {
        val id = 1
        subject.onItemClick(id)

        val event = subject.itemClickEvent.getOrAwaitValue()
        assertThat(event.getContentIfNotHandled(), `is`(id))
    }

    @Test
    fun onItemLongClick_highlightedPositionEquals() {
        val position = 1
        subject.onItemLongClick(position)

        val highlighted = subject.highlightedPosition.getOrAwaitValue()
        assertThat(highlighted, `is`(position))
    }

    @Test
    fun deleteSelectedPaidDate_sizeZero() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val position = 1
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        dataSource.testInsert(PaidDate(date = date3))

        subject.onItemLongClick(position)
        subject.deleteSelectedPaidDate()

        val items = subject.paidDates.getOrAwaitValue()
        assertThat(items.size, `is`(2))
        assertThat(items[0].date, `is`(date1))
        assertThat(items[1].date, `is`(date3))
    }

    @Test
    fun deletePaidDate_positionReset() {
        val date1 = 1602219377796
        val position = 0
        val resetPosition = -1
        dataSource.testInsert(PaidDate(date = date1))

        subject.onItemLongClick(position)
        subject.deleteSelectedPaidDate()

        val highlighted = subject.highlightedPosition.getOrAwaitValue()
        assertThat(highlighted, `is`(resetPosition))
    }

    @Test
    fun itemClick_positionReset() {
        val id = 1
        val date = 1602219377796
        val position = 0
        val resetPosition = -1
        dataSource.testInsert(PaidDate(id = id, date = date))

        subject.onItemLongClick(position)
        subject.onItemClick(id)

        val highlighted = subject.highlightedPosition.getOrAwaitValue()
        assertThat(highlighted, `is`(resetPosition))
    }

    @Test
    fun actionMode_onHighlightedItemClick_navigationEventThrowsException() {
        val id = 1
        val date = 1602219377796
        val position = 0
        dataSource.testInsert(PaidDate(id = id, date = date))

        subject.onItemLongClick(position)
        subject.onItemClick(id)

        try{
            subject.itemClickEvent.getOrAwaitValue()
            fail()
        } catch(e: TimeoutException) {

        }
    }

    @Test
    fun actionMode_onNotHighlightedItemClick_navigationEventThrowsException() {
        val id1 = 1
        val id2 = 2
        val date1 = 1602219377796
        val date2 = 1604123777809
        val position = 0
        dataSource.testInsert(PaidDate(id = id1, date = date1))
        dataSource.testInsert(PaidDate(id = id2, date = date2))

        subject.onItemLongClick(position)
        subject.onItemClick(id2)

        try{
            subject.itemClickEvent.getOrAwaitValue()
            fail()
        } catch(e: TimeoutException) {

        }
    }
}