package com.sergeyrodin.electricitymeter.meterdata.list

import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.MeterData
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

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.dataSource = dataSource
    }

    @After
    fun clearDataSource() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun noItems_noDataTextDisplayed() {
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))
    }

    @Test
    fun addItemClick_nameEquals() {
        val data = "14525"
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data))))
    }

    @Test
    fun addTwoItems_namesDisplayed() {
        val data1 = "14556"
        val data2 = "14579"
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_edit)).perform(typeText(data1))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).perform(replaceText(data2))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data2))))
    }

    @Test
    fun addMeterData_dateDisplayed() {
        val date = System.currentTimeMillis()
        val data = 14611
        dataSource.testInsert(MeterData(data, date = date))
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(dateToString(date)))))
    }

    @Test
    fun twoDataItems_diffDisplayed() {
        val data1 = "14556"
        val data2 = "14579"
        val diff1 = "0"
        val diff2 = "23"
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_edit)).perform(typeText(data1))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).perform(replaceText(data2))
        onView(withId(R.id.add_data_button)).perform(click())

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
        val price = 39.6
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        val context = ApplicationProvider.getApplicationContext<Context>()
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
        val price2 = 14.4
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price1.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price2.toString()))))
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() {
        val data = 14622
        dataSource.testInsert(MeterData(data))
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(R.string.no_items)).check(matches(not(isDisplayed())))
    }

    @Test
    fun onAddItem_clearDataEdit() {
        val data = "14704"
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).check(matches(withText("")))
    }

    @Test
    fun onPaidClick_firstDataNotDisplayed() {
        val data1 = 14622
        val date1 = 1602219377796
        val data2 = 14638
        val date2 = 1604123777809
        dataSource.testInsert(MeterData(data1, date = date1))
        dataSource.testInsert(MeterData(data2, date = date2))
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withId(R.id.paid_button)).perform(click())
        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun historyClick_navigationCalled() {
        val navController = TestNavHostController(getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val scenario = launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)
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
}
