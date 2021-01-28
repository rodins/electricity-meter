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
import org.junit.*
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

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate().getOrAwaitValue()
        assertThat(paidDateFromDb.date, `is`(date))
    }

    @Test
    fun deletePaidDate_equalsNull() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        meterDataDatabase.meterDataDatabaseDao.insertPaidDate(paidDate)

        val paidDateToDelete = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate().getOrAwaitValue()

        meterDataDatabase.meterDataDatabaseDao.deletePaidDate(paidDateToDelete)

        val paidDateFromDb = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate().getOrAwaitValue()
        assertThat(paidDateFromDb, `is`(nullValue()))
    }

    @Test
    fun oneDate_getMeterDataBetweenDates() = runBlockingTest {
        val data = 14314
        val date = 1602219377796
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(data, date = date))

        val items = meterDataDatabase.meterDataDatabaseDao
            .getMeterDataBetweenDates(date, Long.MAX_VALUE).getOrAwaitValue()
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

        val items = meterDataDatabase.meterDataDatabaseDao
            .getMeterDataBetweenDates(date1, date2).getOrAwaitValue()
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

        val items = meterDataDatabase.meterDataDatabaseDao
            .getMeterDataBetweenDates(date2, date3).getOrAwaitValue()
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

        val paidDate = meterDataDatabase.meterDataDatabaseDao.getLastPaidDate().getOrAwaitValue()
        assertThat(paidDate.date, `is`(date2))
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

        val paidDates = meterDataDatabase.meterDataDatabaseDao
            .getPaidDatesRangeById(paidDate2.id).getOrAwaitValue()
        assertThat(paidDates.size, `is`(2))
        assertThat(paidDates[0].date, `is`(date2))
        assertThat(paidDates[1].date, `is`(date3))
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

        val paidDates = meterDataDatabase.meterDataDatabaseDao
            .getPaidDatesRangeById(paidDate4.id).getOrAwaitValue()
        assertThat(paidDates.size, `is`(1))
        assertThat(paidDates[0].date, `is`(date4))
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

        val items = meterDataDatabase.meterDataDatabaseDao
            .getMeterDataBetweenDates(0L, Long.MAX_VALUE).getOrAwaitValue()
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

    @Test
    fun getMeterDataById() = runBlockingTest {
        val id = 1
        val data = 14314
        meterDataDatabase.meterDataDatabaseDao.insert(MeterData(id = id, data = data))

        val meterData = meterDataDatabase.meterDataDatabaseDao.getMeterDataById(id)
        assertThat(meterData?.data, `is`(data))
    }

    @Test
    fun updateMeterData() = runBlockingTest {
        val id = 1
        val data = 14314
        val newData = 14315
        val meterData = MeterData(id = id, data = data)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData)

        meterData.data = newData
        meterDataDatabase.meterDataDatabaseDao.update(meterData)

        val meterDataFromDb = meterDataDatabase.meterDataDatabaseDao.getMeterDataById(id)
        assertThat(meterDataFromDb?.data, `is`(newData))
    }

    @Test
    fun deleteMeterData() = runBlockingTest {
        val id1 = 1
        val data1 = 14314
        val date1 = 1602219377796

        val id2 = 2
        val data2 = 14509
        val date2 = 1604123777809

        val id3 = 3
        val data3 = 14579
        val date3 = 1606715777809

        val id4 = 4
        val data4 = 14638
        val date4 = 1606802177809

        val meterData1 = MeterData(data1, id1, date1)
        val meterData2 = MeterData(data2, id2, date2)
        val meterData3 = MeterData(data3, id3, date3)
        val meterData4 = MeterData(data4, id4, date4)

        meterDataDatabase.meterDataDatabaseDao.insert(meterData1)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData2)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData3)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData4)

        meterDataDatabase.meterDataDatabaseDao.deleteMeterData(meterData2)

        val items = meterDataDatabase.meterDataDatabaseDao
            .getMeterDataBetweenDates(0L, Long.MAX_VALUE).getOrAwaitValue()
        assertThat(items?.size, `is`(3))
        items?.let {
            Assert.assertThat(it[0].data, `is`(data1))
            Assert.assertThat(it[1].data, `is`(data3))
            Assert.assertThat(it[2].data, `is`(data4))
        }
    }

    @Test
    fun getLastMeterDataTest() = runBlockingTest {
        val id1 = 1
        val data1 = 14314
        val date1 = 1602219377796

        val id2 = 2
        val data2 = 14509
        val date2 = 1604123777809

        val id3 = 3
        val data3 = 14579
        val date3 = 1606715777809

        val id4 = 4
        val data4 = 14638
        val date4 = 1606802177809

        val meterData1 = MeterData(data1, id1, date1)
        val meterData2 = MeterData(data2, id2, date2)
        val meterData3 = MeterData(data3, id3, date3)
        val meterData4 = MeterData(data4, id4, date4)

        meterDataDatabase.meterDataDatabaseDao.insert(meterData1)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData2)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData3)
        meterDataDatabase.meterDataDatabaseDao.insert(meterData4)

        val lastMeterData = meterDataDatabase.meterDataDatabaseDao.getLastMeterData()
        assertThat(lastMeterData?.data, `is`(data4))
    }
}