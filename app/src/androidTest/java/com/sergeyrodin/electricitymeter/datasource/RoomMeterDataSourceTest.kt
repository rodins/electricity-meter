package com.sergeyrodin.electricitymeter.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.getOrAwaitValue
import com.sergeyrodin.electricitymeter.utils.dateToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val PRICE = Price(1, 1.68)

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
        runBlockingTest {
            dataSource.insertPrice(PRICE)
        }
    }

    @Test
    fun insertAndGetPaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate)

        val paidDateFromDb = dataSource.getLastObservablePaidDate().getOrAwaitValue()
        assertThat(paidDateFromDb.date, `is`(paidDate.date))
    }

    @Test
    fun deletePaidDate() = runBlockingTest {
        val date = 1602219377796
        val paidDate = PaidDate(date = date, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate)

        val paidDateToDelete = dataSource.getLastObservablePaidDate().getOrAwaitValue()

        dataSource.deletePaidDate(paidDateToDelete)

        val paidDateDeleted = dataSource.getLastObservablePaidDate().getOrAwaitValue()
        assertThat(paidDateDeleted, `is`(nullValue()))
    }

    @Test
    fun oneDate_getMeterDataBetweenDates() = runBlockingTest {
        val data = 14314
        val date = 1602219377796
        dataSource.insertMeterData(MeterData(data, date = date))

        val items = dataSource.getObservableMeterDataByDates(date, Long.MAX_VALUE).getOrAwaitValue()
        assertThat(items.size, `is`(1))
    }

    @Test
    fun twoDates_getMeterDataBetweenDates() = runBlockingTest {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val items = dataSource.getObservableMeterDataByDates(date1, date2).getOrAwaitValue()
        assertThat(items.size, `is`(2))
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
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))

        val items = dataSource.getObservableMeterDataByDates(date2, date3).getOrAwaitValue()
        assertThat(items[0].data, `is`(data2))
        assertThat(items[1].data, `is`(data3))
    }

    @Test
    fun getPaidDates_sizeEquals() = runBlockingTest {
        val date = 1602219377796L
        dataSource.insertPaidDate(PaidDate(date = date, priceId = PRICE.id))

        val items = dataSource.getObservablePaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(1))
    }

    @Test
    fun getPaidDatesRange() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = 1606802177809

        val paidDate1 = PaidDate(1, date1, PRICE.id)
        val paidDate2 = PaidDate(2, date2, PRICE.id)
        val paidDate3 = PaidDate(3, date3, PRICE.id)
        val paidDate4 = PaidDate(4, date4, PRICE.id)

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)
        dataSource.insertPaidDate(paidDate3)
        dataSource.insertPaidDate(paidDate4)

        val items = dataSource.getObservablePaidDatesRangeById(paidDate2.id).getOrAwaitValue()
        assertThat(items.size, `is`(2))
    }

    @Test
    fun deleteAllMeterData() = runBlockingTest {
        val data1 = 14314
        val data2 = 14509
        val data3 = 14579
        val data4 = 14638
        dataSource.insertMeterData(MeterData(data1))
        dataSource.insertMeterData(MeterData(data2))
        dataSource.insertMeterData(MeterData(data3))
        dataSource.insertMeterData(MeterData(data4))

        dataSource.deleteAllMeterData()

        val items = dataSource.getObservableMeterDataByDates(0L, Long.MAX_VALUE).getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun deleteAllPaidDates() = runBlockingTest {
        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = 1606802177809

        val paidDate1 = PaidDate(1, date1, PRICE.id)
        val paidDate2 = PaidDate(2, date2, PRICE.id)
        val paidDate3 = PaidDate(3, date3, PRICE.id)
        val paidDate4 = PaidDate(4, date4, PRICE.id)

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)
        dataSource.insertPaidDate(paidDate3)
        dataSource.insertPaidDate(paidDate4)

        dataSource.deleteAllPaidDates()

        val items = dataSource.getObservablePaidDates().getOrAwaitValue()
        assertThat(items.size, `is`(0))
    }

    @Test
    fun getMeterDataById() = runBlockingTest {
        val id = 1
        val data = 14314
        dataSource.insertMeterData(MeterData(id = id, data = data))

        val meterData = dataSource.getMeterDataById(id)
        assertThat(meterData?.data, `is`(data))
    }

    @Test
    fun updateMeterData() = runBlockingTest {
        val id = 1
        val data = 14314
        val newData = 14315
        val meterData = MeterData(id = id, data = data)
        dataSource.insertMeterData(meterData)

        meterData.data = newData
        dataSource.updateMeterData(meterData)

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

        dataSource.insertMeterData(meterData1)
        dataSource.insertMeterData(meterData2)
        dataSource.insertMeterData(meterData3)
        dataSource.insertMeterData(meterData4)

        dataSource.deleteMeterData(meterData2)

        val items = dataSource.getObservableMeterDataByDates(0L, Long.MAX_VALUE).getOrAwaitValue()

        assertThat(items.size, `is`(3))
        assertThat(items[0].data, `is`(data1))
        assertThat(items[1].data, `is`(data3))
        assertThat(items[2].data, `is`(data4))
    }

    @Test
    fun getLastMeterData() = runBlockingTest {
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

        dataSource.insertMeterData(meterData1)
        dataSource.insertMeterData(meterData2)
        dataSource.insertMeterData(meterData3)
        dataSource.insertMeterData(meterData4)

        val meterData = dataSource.getLastMeterData()
        assertThat(meterData?.data, `is`(data4))
    }

    @Test
    fun savePrice_priceEquals() {
        val priceFromDb = dataSource.getFirstObservablePrice().getOrAwaitValue()
        assertThat(priceFromDb.price, `is`(PRICE.price))
    }

    @Test
    fun deletePrice_priceCountZero() = runBlockingTest {
        dataSource.deletePrices()

        val priceCount = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(priceCount, `is`(0))
    }

    @Test
    fun getMeterDataByDate() = runBlockingTest {
        val date = dateToLong(2020, 12, 1, 9, 0)
        val data = 14704
        dataSource.insertMeterData(MeterData(data, date = date))

        val meterData = dataSource.getMeterDataByDate(date)
        assertThat(meterData?.date, `is`(date))
    }

    @Test
    fun getFirstMeterData() = runBlockingTest {
        val date1 = dateToLong(2020, 12, 1, 9, 0)
        val data1 = 14704
        val meterData1 = MeterData(data1, date = date1)

        val date2 = dateToLong(2020, 12, 30, 9, 0)
        val data2 = 15123
        val meterData2 = MeterData(data2, date = date2)

        dataSource.insertMeterData(meterData1)
        dataSource.insertMeterData(meterData2)

        val firstMeterData = dataSource.getFirstMeterData()
        assertThat(firstMeterData?.data, `is`(meterData1.data))
    }

    @Test
    fun getPrice_priceEquals() = runBlockingTest {
        val priceFromDb = dataSource.getPrice()
        assertThat(priceFromDb?.price, `is`(PRICE.price))
    }

    @Test
    fun getObservablePriceById() = runBlockingTest {
        val price2 = Price(2, 2.0)
        dataSource.insertPrice(price2)

        val priceFromDb = dataSource.getObservablePriceById(price2.id).getOrAwaitValue()
        assertThat(priceFromDb.price, `is`(price2.price))
    }

    @Test
    fun onePrice_getLastObservablePrice() = runBlockingTest {
        val lastPrice = dataSource.getLastObservablePrice().getOrAwaitValue()
        assertThat(lastPrice.price, `is`(PRICE.price))
    }

    @Test
    fun getObservablePrices() = runBlockingTest {
        val price2 = Price(2, 2.0)
        dataSource.insertPrice(price2)

        val prices = dataSource.getObservablePrices().getOrAwaitValue()
        assertThat(prices.size, `is`(2))
    }

    @Test
    fun deletePrice_sizeZero() = runBlockingTest {
        dataSource.deletePrice(PRICE)

        val prices = dataSource.getObservablePrices().getOrAwaitValue()
        assertThat(prices.size, `is`(0))
    }

    @Test
    fun getPaidDatesCountByPriceId() = runBlockingTest {
        val paidDate = PaidDate(1, System.currentTimeMillis(), PRICE.id)
        dataSource.insertPaidDate(paidDate)

        val count = dataSource.getPaidDatesCountByPriceId(PRICE.id)
        assertThat(count, `is`(1))
    }
}