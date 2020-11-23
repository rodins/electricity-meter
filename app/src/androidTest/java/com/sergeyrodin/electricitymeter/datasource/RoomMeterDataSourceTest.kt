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

@RunWith(AndroidJUnit4::class)
@SmallTest
class RoomMeterDataSourceTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: MeterDataSource

    @Before
    fun createDataSource() {
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), MeterDataDatabase::class.java
        ).allowMainThreadQueries().build()
        dataSource = RoomMeterDataSource(database.meterDataDatabaseDao)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetMeterData() = runBlockingTest{
        val data = 14622
        val meterData = MeterData(data)

        dataSource.insert(meterData)

        val meterDataFromDb = dataSource.getMeterData().getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data))
    }
}