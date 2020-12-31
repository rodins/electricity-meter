package com.sergeyrodin.electricitymeter.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RoomMeterDataSourceTest {
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
    fun insertAndGetPaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        dataSource.insertPaidDate(paidDate)

        val paidDateFromDb = dataSource.getLastPaidDate()
        assertThat(paidDate.date, `is`(paidDateFromDb?.date))
    }

    @Test
    fun deletePaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date)

        dataSource.insertPaidDate(paidDate)

        val paidDateToDelete = dataSource.getLastPaidDate()

        dataSource.deletePaidDate(paidDateToDelete)

        val paidDateDeleted = dataSource.getLastPaidDate()
        assertThat(paidDateDeleted, `is`(nullValue()))
    }

    @Test
    fun oneDate_getMeterDataBetweenDates() = runBlockingTest {
        val data = 14314
        val date = 1602219377796
        dataSource.insert(MeterData(data, date = date))

        val items = dataSource.getMeterDataBetweenDates(date, Long.MAX_VALUE)
        assertThat(items?.size, `is`(1))
    }

    @Test
    fun twoDates_getMeterDataBetweenDates() = runBlockingTest {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))

        val items = dataSource.getMeterDataBetweenDates(date1, date2)
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))

        val items = dataSource.getMeterDataBetweenDates(date2, date3)
        assertThat(items?.get(0)?.data, `is`(data2))
        assertThat(items?.get(1)?.data, `is`(data3))
    }

    @Test
    fun getPaidDates_sizeEquals() = runBlockingTest {
        val date = 1602219377796L
        dataSource.insertPaidDate(PaidDate(date = date))

        val items = dataSource.getPaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(1))
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

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)
        dataSource.insertPaidDate(paidDate3)
        dataSource.insertPaidDate(paidDate4)

        val items = dataSource.getPaidDatesRangeById(paidDate2.id)
        assertThat(items?.size, `is`(2))
    }

    @Test
    fun deleteAllMeterData() = runBlockingTest {
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.insert(MeterData(data1))
        dataSource.insert(MeterData(data2))
        dataSource.insert(MeterData(data3))
        dataSource.insert(MeterData(data4))

        dataSource.deleteAllMeterData()

        val items = dataSource.getMeterDataBetweenDates(0L, Long.MAX_VALUE)
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

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)
        dataSource.insertPaidDate(paidDate3)
        dataSource.insertPaidDate(paidDate4)

        dataSource.deleteAllPaidDates()

        val items = dataSource.getPaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun getMeterDataById() = runBlockingTest {
        val id = 1
        val data = 14314
        dataSource.insert(MeterData(id = id, data = data))

        val meterData = dataSource.getMeterDataById(id)
        assertThat(meterData?.data, `is`(data))
    }

    @Test
    fun updateMeterData() = runBlockingTest {
        val id = 1
        val data = 14314
        val newData = 14315
        val meterData = MeterData(id = id, data = data)
        dataSource.insert(meterData)

        meterData.data = newData
        dataSource.update(meterData)

        val meterDataFromDb = dataSource.getMeterDataById(id)
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

        dataSource.insert(meterData1)
        dataSource.insert(meterData2)
        dataSource.insert(meterData3)
        dataSource.insert(meterData4)

        dataSource.deleteMeterData(meterData2)

        val items = dataSource.getMeterDataBetweenDates(0L, Long.MAX_VALUE)
        assertThat(items?.size, `is`(3))
        items?.let {
            assertThat(it[0].data, `is`(data1))
            assertThat(it[1].data, `is`(data3))
            assertThat(it[2].data, `is`(data4))
        }
    }
}