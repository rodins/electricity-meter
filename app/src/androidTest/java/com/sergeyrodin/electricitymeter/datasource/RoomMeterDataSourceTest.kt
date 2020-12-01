package com.sergeyrodin.electricitymeter.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RoomMeterDataSourceTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: RoomMeterDataSource

    @Before
    fun createDataSource() {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), MeterDataDatabase::class.java
        ).allowMainThreadQueries().build()
        dataSource = RoomMeterDataSource(database.meterDataDatabaseDao)
    }

    @Test
    fun insertAndGetMeterData() = runBlockingTest{
        val data = 14622
        val meterData = MeterData(data)

        dataSource.insert(meterData)

        val meterDataFromDb = dataSource.getMeterData().getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data))
    }

    @Test
    fun insertAndGetMonthMeterDataNow() = runBlockingTest{
        val data = 14622
        val meterData = MeterData(data)

        dataSource.insert(meterData)

        val meterDataFromDb = dataSource.getMonthMeterData(System.currentTimeMillis()).getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data))
    }

    @Test
    fun filterCurrentMonth() = runBlockingTest{
        val data1 = 14314
        val someDayOfPrevMonth = 1602219377796
        val data2 = 14509
        val lastDayOfPrevMonth = 1604123777809
        val data3 = 14579
        val lastDayOfCurrentMonth = 1606715777809
        val data4 = 14638
        val firstDayOfNextMonth = 1606802177809

        dataSource.insert(MeterData(data1, date = someDayOfPrevMonth))
        dataSource.insert(MeterData(data2, date = lastDayOfPrevMonth))
        dataSource.insert(MeterData(data3, date = lastDayOfCurrentMonth))
        dataSource.insert(MeterData(data4, date = firstDayOfNextMonth))

        val meterDataFromDb = dataSource.getMonthMeterData(lastDayOfCurrentMonth).getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data2))
        assertThat(meterDataFromDb[1].data, `is`(data3))
    }
}