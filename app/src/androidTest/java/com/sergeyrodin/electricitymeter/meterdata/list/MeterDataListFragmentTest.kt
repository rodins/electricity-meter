package com.sergeyrodin.electricitymeter.meterdata.list

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
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
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MeterDataListFragmentTest {
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
    fun noItems_noDataTextDisplayed() {
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))
    }

    @Test
    fun addMeterData_dateDisplayed() {
        val date = System.currentTimeMillis()
        val data = 14611
        dataSource.testInsert(MeterData(data, date = date))
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)

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
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff2))))
    }

    @Test
    fun fewItems_totalAvgPriceDisplayed() {
        val data1 = 14594
        val data2 = 14611
        val data3 = 14622
        val data4 = 14638
        dataSource.testInsert(MeterData(data1))
        dataSource.testInsert(MeterData(data2))
        dataSource.testInsert(MeterData(data3))
        dataSource.testInsert(MeterData(data4))
        val total = 44
        val avg = 14
        val price = 73.92
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)

        val context = getApplicationContext<Context>()
        val totalValue = context.resources.getString(R.string.total_format, total, avg, price)

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
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price1.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price2.toString()))))
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() {
        val data = 14622
        dataSource.testInsert(MeterData(data))
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
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
        val scenario = launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
        val actionPaid = ActionMenuItem(
            getApplicationContext(),
            0,
            R.id.action_paid,
            0,
            0,
            null
        )

        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(actionPaid)
        }

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun historyClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val scenario = launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
        val historyMenuItem = ActionMenuItem(
            getApplicationContext(),
            0,
            R.id.paidListFragment,
            0,
            0,
            null)

        scenario.onFragment{ fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
            fragment.onOptionsItemSelected(historyMenuItem)
        }

        assertThat(navController.currentDestination?.id, `is`(R.id.paidListFragment))
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

        val args = MeterDataListFragmentArgs(paidDate1.id).toBundle()
        launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data4.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data5.toString())).check(doesNotExist())
    }

    @Test
    fun addMeterDataClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val scenario = launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
        scenario.onFragment{ fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.add_meter_data_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.addEditMeterDataFragment))
    }

    @Test
    fun meterDataItemClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val data = 14314
        dataSource.testInsert(MeterData(data))
        val scenario = launchFragmentInContainer<MeterDataListFragment>(args, R.style.Theme_ElectricityMeter)
        scenario.onFragment{ fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withSubstring(data.toString())).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.addEditMeterDataFragment))
    }
}
