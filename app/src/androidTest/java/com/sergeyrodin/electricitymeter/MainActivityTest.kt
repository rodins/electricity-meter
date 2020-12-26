package com.sergeyrodin.electricitymeter

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.datasource.RoomMeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import com.sergeyrodin.electricitymeter.utils.DataBindingIdlingResource
import com.sergeyrodin.electricitymeter.utils.EspressoIdlingResource
import com.sergeyrodin.electricitymeter.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var dataSource: MeterDataSource

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Before
    fun initDataSource() {
        dataSource = ServiceLocator.provideMeterDataSource(getApplicationContext())
        runBlocking {
            dataSource.deleteAllMeterData()
            dataSource.deleteAllPaidDates()
        }
    }

    @After
    fun clearDatabase() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun onHistoryClick_noDataDisplayed() = runBlocking {
        val data = 14622
        dataSource.insert(MeterData(data))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun paidClicked_dateEquals() = runBlocking {
        val data = 14622
        val date = 1602219377796
        dataSource.insert(MeterData(data, date = date))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paid_button)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun twoDatesPaid_datesEqual() = runBlocking {
        val data1 = 14622
        val date1 = 1602219377796
        val data2 = 14638
        val date2 = System.currentTimeMillis()
        dataSource.insert(MeterData(data1, date = date1))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paid_button)).perform(click())

        onView(withId(R.id.data_edit)).perform(typeText(data2.toString()))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.paid_button)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())

        onView(withText(dateToString(date1))).check(matches(isDisplayed()))
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun displayMeterDataByPaidDate() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

        val paidDate1 = PaidDate(date = date2)
        val paidDate2 = PaidDate(date = date4)

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data4.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data5.toString())).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun displayMeterDataBySinglePaidDate() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

        val paidDate = PaidDate(date = date2)

        dataSource.insertPaidDate(paidDate)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data4.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data5.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }


}