package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate

interface MeterDataSource {
    suspend fun insert(meterData: MeterData)
    fun getObservableData(beginDate: Long = 0L, endDate: Long = Long.MAX_VALUE): LiveData<List<MeterData>>
    suspend fun insertPaidDate(paidDate: PaidDate)
    fun getLastPaidDate(): LiveData<PaidDate>
    suspend fun deletePaidDate(paidDate: PaidDate?)
    fun getPaidDates(): LiveData<List<PaidDate>>
    fun getPaidDatesRangeById(id: Int): LiveData<List<PaidDate>>
    suspend fun deleteAllMeterData()
    suspend fun deleteAllPaidDates()
    suspend fun getMeterDataById(id: Int): MeterData?
    suspend fun update(meterData: MeterData)
    suspend fun deleteMeterData(meterData: MeterData)
    suspend fun getLastMeterData(): MeterData?
}