package com.sergeyrodin.electricitymeter.history

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MeterDataHistoryFragmentTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var dataSource: FakeDataSource
    private lateinit var args: Bundle

    @Before
    fun initDataSource() {
        args = Bundle()
        dataSource = FakeDataSource()
        ServiceLocator.dataSource = dataSource
    }

    @After
    fun clearDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun paidDateIdArgs_filteredMeterDataDisplayed() {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971

        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        dataSource.testInsert(MeterData(data3, date = date3))
        dataSource.testInsert(MeterData(data4, date = date4))
        dataSource.testInsert(MeterData(data5))

        val paidDate1 = PaidDate(1, date2)
        val paidDate2 = PaidDate(2, date4)

        dataSource.testInsert(paidDate1)
        dataSource.testInsert(paidDate2)

        val args = MeterDataHistoryFragmentArgs(paidDate1.id).toBundle()
        launchFragmentInContainer<MeterDataHistoryFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withSubstring(data1.toString()))
            .check(doesNotExist())
        onView(withSubstring(data2.toString()))
            .check(matches(isDisplayed()))
        onView(withSubstring(data3.toString()))
            .check(matches(isDisplayed()))
        onView(withSubstring(data4.toString()))
            .check(matches(isDisplayed()))
        onView(withSubstring(data5.toString()))
            .check(doesNotExist())
    }
}