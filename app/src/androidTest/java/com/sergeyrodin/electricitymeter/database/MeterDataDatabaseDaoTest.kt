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
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
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


    @Test
    fun insertAndGetMeterData() = runBlockingTest {
        val data = 14622
        val meterData = MeterData(data)

        meterDataDatabase.meterDataDatabaseDao.insert(meterData)

        val meterDataFromDb = meterDataDatabase
            .meterDataDatabaseDao.getMeterData().getOrAwaitValue()
        assertThat(meterDataFromDb[0].data, `is`(data))
    }

    @Test
    fun insertAndGetPaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate)

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getPaidDate()
        assertThat(paidDateFromDb?.date, `is`(date))
    }

    @Test
    fun deletePaidDate_equalsNull() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate)

        val paidDateToDelete = meterDataDatabase.meterDataDatabaseDao.getPaidDate()

        meterDataDatabase.meterDataDatabaseDao.deletePaidDate(paidDateToDelete)

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getPaidDate()
        assertThat(paidDateFromDb, `is`(nullValue()))
    }

    @Test
    fun oneDate_getMeterDataBetweenDates() = runBlockingTest {
        val data = 14314
        val date = 1602219377796
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data, date = date))

        val items = meterDataDatabase.meterDataDatabaseDao.getMeterDataBetweenDates(date, Long.MAX_VALUE)
        assertThat(items?.size, `is`(1))
    }

    @Test
    fun twoDates_getMeterDataBetweenDates() = runBlockingTest {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data1, date = date1))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data2, date = date2))

        val items = meterDataDatabase.meterDataDatabaseDao.getMeterDataBetweenDates(date1, date2)
        assertThat(items?.size, `is`(2))
    }

    @Test
    fun fourDates_getMeterDataBetweenDates() = runBlockingTest {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data1, date = date1))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data2, date = date2))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data3, date = date3))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data4, date = date4))

        val items = meterDataDatabase.meterDataDatabaseDao.getMeterDataBetweenDates(date2, date3)
        assertThat(items?.get(0)?.data, `is`(data2))
        assertThat(items?.get(1)?.data, `is`(data3))
    }

    @Test
    fun getPaidDates_sizeEquals() = runBlockingTest {
        val date = 1602219377796L
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(PaidDate(date = date))

        val items = meterDataDatabase.meterDataDatabaseDao.getPaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(1))
    }
}