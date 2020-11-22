package com.sergeyrodin.electricitymeter.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MeterDataDatabaseDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var meterDataDatabase: MeterDataDatabase

    @Before
    fun initDatabase() {
        meterDataDatabase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(), MeterDataDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun clearDatabase() {
        meterDataDatabase.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetMeterData() = runBlockingTest{
        val data = 14622
        val meterData = MeterData(data)

        meterDataDatabase.meterDataDatabaseDao.insert(meterData)

        val meterDataFromDb = meterDataDatabase
            .meterDataDatabaseDao.getMeterData().getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data))
    }
}