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

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(data))))

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

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(data1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(data2))))

        activityScenario.close()
    }

    @Test
    fun addMeterData_dateDisplayed() = runBlocking {
        val date = System.currentTimeMillis()
        val data = 14611
        val meterData = MeterData(data, date = date)
        dataSource.insert(meterData)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(dateToString(date)))))

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

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(diff1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(diff2))))

        activityScenario.close()
    }

    @Test
    fun fewItems_totalAndAvgDisplayed() = runBlocking() {
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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        val context = getApplicationContext<Context>()
        val totalValue = context.resources.getString(R.string.total_format, total, avg)

        onView(withText(totalValue)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}