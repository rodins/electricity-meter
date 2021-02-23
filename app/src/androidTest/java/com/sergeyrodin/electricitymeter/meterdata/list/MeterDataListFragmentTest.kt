package com.sergeyrodin.electricitymeter.meterdata.list

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.di.MeterDataSourceModule
import com.sergeyrodin.electricitymeter.launchFragmentInHiltContainer
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import com.sergeyrodin.electricitymeter.utils.dateToLong
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

private const val YEAR = 2021
private const val MONTH = 1

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(MeterDataSourceModule::class)
class MeterDataListFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun noItems_noDataTextDisplayed() {
        launchFragmentInHiltContainer<MeterDataListFragment>()
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))
    }

    @Test
    fun addMeterData_dateDisplayed() {
        val date = System.currentTimeMillis()
        val data = 14611
        dataSource.testInsert(MeterData(data, date = date))
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(dateToString(date)))))
    }

    @Test
    fun twoDataItems_diffDisplayed() {
        val data1 = 14556
        val data2 = 14579
        val diff1 = "0"
        val diff2 = "23"
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff2))))
    }

    @Test
    fun fewItems_totalAvgPricePrognosisDisplayed() {
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

        dataSource.testInsert(meterData1)
        dataSource.testInsert(meterData2)
        dataSource.testInsert(meterData3)
        dataSource.testInsert(meterData4)
        val total = 81
        val avg = 20
        val price = 136.08
        val prognosis = 1029.84
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        val context = getApplicationContext<Context>()
        val totalValue = context.resources.getString(R.string.total_format, total, avg, price, prognosis)

        onView(withText(totalValue)).check(matches(isDisplayed()))
    }

    @Test
    fun twoItems_dailyPriceDisplayed() {
        val data1 = 14622
        val data2 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        val price1 = 0.0
        val price2 = 26.88
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price1.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price2.toString()))))
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() {
        val data = 14622
        dataSource.testInsert(MeterData(data))
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)
        onView(withText(R.string.no_items)).check(matches(not(isDisplayed())))
    }

    @Test
    fun onPaidClick_firstDataNotDisplayed() {
        val data1 = 14622
        val date1 = 1602219377796
        val data2 = 14638
        val date2 = 1604123777809
        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter) {
            val actionPaid = ActionMenuItem(
                getApplicationContext(),
                0,
                R.id.action_paid,
                0,
                0,
                null
            )

            onOptionsItemSelected(actionPaid)
        }

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun historyClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter) {
            val historyMenuItem = ActionMenuItem(
                getApplicationContext(),
                0,
                R.id.paidListFragment,
                0,
                0,
                null)

            Navigation.setViewNavController(requireView(), navController)
            onOptionsItemSelected(historyMenuItem)
        }

        assertThat(navController.currentDestination?.id, `is`(R.id.paidListFragment))
    }


    @Test
    fun addMeterDataClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.add_meter_data_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.addMeterDataFragment))
    }

    @Test
    fun meterDataItemClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val data = 14314
        dataSource.testInsert(MeterData(data))
        launchFragmentInHiltContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withSubstring(data.toString())).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.addEditMeterDataFragment))
    }

}
