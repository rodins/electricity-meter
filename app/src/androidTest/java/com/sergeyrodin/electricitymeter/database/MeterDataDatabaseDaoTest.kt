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
    fun insertAndGetPaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate)

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate()
        assertThat(paidDateFromDb?.date, `is`(date))
    }

    @Test
    fun deletePaidDate_equalsNull() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate)

        val paidDateToDelete = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate()

        meterDataDatabase.meterDataDatabaseDao.deletePaidDate(paidDateToDelete)

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate()
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

    @Test
    fun getLastPaidDate() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(PaidDate(date = date1))
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(PaidDate(date = date2))

        val paidDate = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate()
        assertThat(paidDate?.date, `is`(date2))
    }

    @Test
    fun getPaidDatesRange() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = 1606802177809

        val paidDate1 = PaidDate(1, date1)
        val paidDate2 = PaidDate(2, date2)
        val paidDate3 = PaidDate(3, date3)
        val paidDate4 = PaidDate(4, date4)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate1)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate2)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate3)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate4)

        val paidDates = meterDataDatabase.meterDataDatabaseDao.getPaidDatesRangeById(paidDate2.id)
        assertThat(paidDates?.size, `is`(2))
        assertThat(paidDates?.get(0)?.date, `is`(date2))
        assertThat(paidDates?.get(1)?.date, `is`(date3))
    }

    @Test
    fun getPaidDatesRangeLastElement() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = 1606802177809

        val paidDate1 = PaidDate(1, date1)
        val paidDate2 = PaidDate(2, date2)
        val paidDate3 = PaidDate(3, date3)
        val paidDate4 = PaidDate(4, date4)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate1)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate2)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate3)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate4)

        val paidDates = meterDataDatabase.meterDataDatabaseDao.getPaidDatesRangeById(paidDate4.id)
        assertThat(paidDates?.size, `is`(1))
        assertThat(paidDates?.get(0)?.date, `is`(date4))
    }

    @Test
    fun deleteAllMeterData() = runBlockingTest {
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data1))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data2))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data3))
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data4))

        meterDataDatabase.meterDataDatabaseDao.deleteAllMeterData()

        val items = meterDataDatabase.meterDataDatabaseDao.getMeterDataBetweenDates(0L, Long.MAX_VALUE)
        assertThat(items?.size, `is`(0))
    }

    @Test
    fun deleteAllPaidDates() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = 1606802177809

        val paidDate1 = PaidDate(1, date1)
        val paidDate2 = PaidDate(2, date2)
        val paidDate3 = PaidDate(3, date3)
        val paidDate4 = PaidDate(4, date4)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate1)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate2)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate3)
        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate4)

        meterDataDatabase.meterDataDatabaseDao.deleteAllPaidDates()

        val items = meterDataDatabase.meterDataDatabaseDao.getPaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }
}