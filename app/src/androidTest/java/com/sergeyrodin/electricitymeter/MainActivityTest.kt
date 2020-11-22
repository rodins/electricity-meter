package com.sergeyrodin.electricitymeter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun clearData() {
        DataHolder.clear()
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
    fun addMeterData_dateDisplayed() {
        val date = System.currentTimeMillis()
        val data = 14611
        val meterData = MeterData(data, date = date)
        DataHolder.insert(meterData)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withText(dateToString(date)))))

        activityScenario.close()
    }
}