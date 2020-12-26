package com.sergeyrodin.electricitymeter.datasource

import androidx.lifecycle.LiveData
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate

interface MeterDataSource {
    suspend fun insert(meterData: MeterData)
    suspend fun getMeterDataBetweenDates(beginDate: Long, endDate: Long): List<MeterData>?
    suspend fun insertPaidDate(paidDate: PaidDate)
    suspend fun getLastPaidDate(): PaidDate?
    suspend fun deletePaidDate(paidDate: PaidDate?)
    fun getPaidDates(): LiveData<List<PaidDate>>
    suspend fun getPaidDatesRangeById(id: Int): List<PaidDate>?
    suspend fun deleteAllMeterData()
    suspend fun deleteAllPaidDates()
}