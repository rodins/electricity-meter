package com.sergeyrodin.electricitymeter.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.di.MeterDataSourceModule
import com.sergeyrodin.electricitymeter.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

private val PRICE = Price(1, 1.68)

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(MeterDataSourceModule::class)
class MeterDataHistoryFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
        dataSource.insertPriceBlocking(PRICE)
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

        dataSource.insertMeterDataBlocking(MeterData(data1, date = date1))
        dataSource.insertMeterDataBlocking(MeterData(data2, date = date2))
        dataSource.insertMeterDataBlocking(MeterData(data3, date = date3))
        dataSource.insertMeterDataBlocking(MeterData(data4, date = date4))
        dataSource.insertMeterDataBlocking(MeterData(data5))

        val paidDate1 = PaidDate(1, date2, PRICE.id)
        val paidDate2 = PaidDate(2, date4, PRICE.id)

        dataSource.insertPaidDateBlocking(paidDate1)
        dataSource.insertPaidDateBlocking(paidDate2)

        val args = MeterDataHistoryFragmentArgs(paidDate1.id).toBundle()
        launchFragmentInHiltContainer<MeterDataHistoryFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withSubstring(data1.toString()))
            .check(matches(isDisplayed()))
        onView(withSubstring(data2.toString()))
            .check(matches(isDisplayed()))
        onView(withSubstring(data3.toString()))
            .check(doesNotExist())
        onView(withSubstring(data4.toString()))
            .check(doesNotExist())
        onView(withSubstring(data5.toString()))
            .check(doesNotExist())
    }
}