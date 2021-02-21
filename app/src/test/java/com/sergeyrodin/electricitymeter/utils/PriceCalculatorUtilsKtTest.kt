package com.sergeyrodin.electricitymeter.utils

import com.sergeyrodin.electricitymeter.database.MeterData
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

private const val YEAR = 2021
private const val MONTH = 1

class PriceCalculatorUtilsKtTest {

    @Test
    fun prognosisByDates_noItems_prognosisZero() {
        val items = listOf<MeterData>()
        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(0.0))
    }

    @Test
    fun prognosisByDates_twoItems_prognosisEquals() {
        val date1 = dateToLong(YEAR, MONTH,19, 9, 0)
        val data1 = 15142
        val meterData1 = MeterData(data1, 1, date1)

        val date2 = dateToLong(YEAR, MONTH,20, 8, 5)
        val data2 = 15169
        val meterData2 = MeterData(data2, 2, date2)

        val items = listOf(meterData1, meterData2)

        val expected = 1419.6

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }

    @Test
    fun prognosisByDates_fourItems_prognosisEquals() {
        val date1 = dateToLong(YEAR, MONTH,19, 9, 0)
        val data1 = 15142
        val meterData1 = MeterData(data1, 1, date1)

        val date2 = dateToLong(YEAR, MONTH,20, 8, 10)
        val data2 = 15169
        val meterData2 = MeterData(data2, 2, date2)

        val date3 = dateToLong(YEAR, MONTH,22, 9, 5)
        val data3 = 15195
        val meterData3 = MeterData(data3, 3, date3)

        val date4 = dateToLong(YEAR, MONTH,23, 8, 30)
        val data4 = 15223
        val meterData4 = MeterData(data4, 4, date4)

        val items = listOf(meterData1, meterData2, meterData3, meterData4)

        val expected = 1029.84

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }

    @Test
    fun prognosisByDates_twoItems_currentDates_prognosisZero() {
        val data1 = 15142
        val meterData1 = MeterData(data1)

        val data2 = 15169
        val meterData2 = MeterData(data2)

        val items = listOf(meterData1, meterData2)

        val expected = 0.0

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }

    @Test
    fun prognosisByDates_oneItem_prognosisZero() {
        val data1 = 15142
        val meterData1 = MeterData(data1)

        val items = listOf(meterData1)

        val expected = 0.0

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }

    @Test
    fun prognosisByDates_twoItems_oneHourDiff_prognosisEquals() {
        val date1 = dateToLong(YEAR, MONTH,19, 8, 0)
        val data1 = 15142
        val meterData1 = MeterData(data1, 1, date1)

        val date2 = dateToLong(YEAR, MONTH,19, 9, 5)
        val data2 = 15143
        val meterData2 = MeterData(data2, 2, date2)

        val items = listOf(meterData1, meterData2)

        val expected = 1209.6

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }

    @Test
    fun prognosisByDates_twoItems_negativeDiffHours_prognosisZero() {
        val date1 = dateToLong(YEAR, MONTH,19, 9, 0)
        val data1 = 15142
        val meterData1 = MeterData(data1, 1, date1)

        val date2 = dateToLong(YEAR, MONTH,19, 8, 5)
        val data2 = 15143
        val meterData2 = MeterData(data2, 2, date2)

        val items = listOf(meterData1, meterData2)

        val expected = 0.0

        val prognosis = calculatePrognosisByDates(items)
        assertThat(prognosis, `is`(expected))
    }
}