package com.sergeyrodin.electricitymeter.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MeterDataDatabaseDao {

    @Insert
    suspend fun insert(meterData: MeterData)

    @Insert
    suspend fun insertPaidDate(paidDate: PaidDate)

    @Insert
    fun insertPaidDateBlocking(paidDate: PaidDate)

    @Query("SELECT * FROM paid_dates ORDER BY id DESC LIMIT 1")
    fun getLastPaidDate(): LiveData<PaidDate>

    @Delete
    suspend fun deletePaidDate(paidDate: PaidDate?)

    @Query("SELECT * FROM meter_data WHERE date BETWEEN :beginDate AND :endDate")
    fun getObservableMeterDataBetweenDates(beginDate: Long, endDate: Long): LiveData<List<MeterData>>

    @Query("SELECT * FROM paid_dates")
    fun getPaidDates(): LiveData<List<PaidDate>>

    @Query("SELECT * FROM paid_dates WHERE id =:id")
    fun getPaidDateByIdBlocking(id: Int): PaidDate?

    @Query("SELECT * FROM paid_dates WHERE id <= :id ORDER BY id DESC LIMIT 2")
    fun getPaidDatesRangeById(id: Int): LiveData<List<PaidDate>>

    @Query("DELETE FROM meter_data")
    suspend fun deleteAllMeterData()

    @Query("DELETE FROM paid_dates")
    suspend fun deleteAllPaidDates()

    @Query("SELECT * FROM meter_data WHERE id = :id")
    suspend fun getMeterDataById(id: Int): MeterData?

    @Query("SELECT * FROM meter_data WHERE id = :id")
    fun getMeterDataByIdBlocking(id: Int): MeterData?

    @Update
    suspend fun update(meterData: MeterData)

    @Delete
    suspend fun deleteMeterData(meterData: MeterData)

    @Query("SELECT * FROM meter_data ORDER BY id DESC LIMIT 1")
    suspend fun getLastMeterData(): MeterData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: Price)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPriceBlocking(price: Price)

    @Query("SELECT * FROM prices LIMIT 1")
    fun getObservablePrice(): LiveData<Price>

    @Query("SELECT count(*) FROM prices")
    fun getObservablePriceCount(): LiveData<Int>

    @Query("SELECT * FROM prices WHERE id = :id")
    fun getPriceByIdBlocking(id: Int): Price?

    @Query("DELETE FROM prices")
    suspend fun deletePrice()

    @Query("SELECT * FROM meter_data WHERE date = :date")
    suspend fun getMeterDataByDate(date: Long): MeterData?

    @Query("SELECT * FROM meter_data LIMIT 1")
    suspend fun getFirstMeterData(): MeterData?

    @Query("SELECT * FROM prices LIMIT 1")
    suspend fun getPrice(): Price?
}