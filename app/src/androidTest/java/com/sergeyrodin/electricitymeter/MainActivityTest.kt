package com.sergeyrodin.electricitymeter

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.MeterDataDatabase
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.datasource.RoomMeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.dateToString
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
    private lateinit var database: MeterDataDatabase
    private lateinit var dataSource: MeterDataSource

    @Before
    fun initDataSource() {
        database = MeterDataDatabase.getInstance(getApplicationContext())
        dataSource = RoomMeterDataSource(database.meterDataDatabaseDao)
    }

    @After
    fun clearDatabase() {
        MeterDataDatabase.reset()
    }

    @Test
    fun addItemClick_nameEquals() {
        val data = "14525"
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data))))

        activityScenario.close()
    }

    @Test
    fun addTwoItems_namesDisplayed() {
        val data1 = "14556"
        val data2 = "14579"

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_edit)).perform(typeText(data1))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).perform(replaceText(data2))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data2))))

        activityScenario.close()
    }

    @Test
    fun addMeterData_dateDisplayed() = runBlocking {
        val date = System.currentTimeMillis()
        val data = 14611
        val meterData = MeterData(data, date = date)
        dataSource.insert(meterData)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(dateToString(date)))))

        activityScenario.close()
    }

    @Test
    fun twoDataItems_diffDisplayed() {
        val data1 = "14556"
        val data2 = "14579"
        val diff1 = "0"
        val diff2 = "23"
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_edit)).perform(typeText(data1))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).perform(replaceText(data2))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(diff2))))

        activityScenario.close()
    }

    @Test
    fun fewItems_totalAvgPriceDisplayed() = runBlocking() {
        val data1 = 14594
        val data2 = 14611
        val data3 = 14622
        val data4 = 14638
        val meterData1 = MeterData(data1)
        val meterData2 = MeterData(data2)
        val meterData3 = MeterData(data3)
        val meterData4 = MeterData(data4)
        dataSource.insert(meterData1)
        dataSource.insert(meterData2)
        dataSource.insert(meterData3)
        dataSource.insert(meterData4)
        val total = 44
        val avg = 14
        val price = 39.6

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        val context = getApplicationContext<Context>()
        val totalValue = context.resources.getString(R.string.total_format, total, avg, price)

        onView(withText(totalValue)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun twoItems_dailyPriceDisplayed() = runBlocking {
        val data1 = 14622
        val data2 = 14638
        dataSource.insert(MeterData(data1))
        dataSource.insert(MeterData(data2))
        val price1 = 0.0
        val price2 = 14.4

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price1.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(price2.toString()))))

        activityScenario.close()
    }

    @Test
    fun noItems_noItemsTextDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withText(R.string.no_items)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun oneItem_noItemsTextNotDisplayed() = runBlocking{
        val data1 = 14622
        dataSource.insert(MeterData(data1))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withText(R.string.no_items)).check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun onAddItem_clearDataEdit() {
        val data = "14704"
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.add_data_button)).perform(click())

        onView(withId(R.id.data_edit)).check(matches(withText("")))

        activityScenario.close()
    }
}