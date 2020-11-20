package com.sergeyrodin.electricitymeter.meterdata.list

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.DataHolder
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MeterDataListFragmentTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @After
    fun clearData() {
        DataHolder.data.clear()
    }

    @Test
    fun addDataClick_navigationCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)

        val scenario = launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment{ fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.add_data_fab)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataInputFragment))
    }

    @Test
    fun oneInputDataItem_dataDisplayed() {
        val data = MeterData(14556)
        DataHolder.data.add(data)
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(data.data.toString())).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun fewInputDataItems_dataDisplayed() {
        val data = listOf(MeterData(14556), MeterData(14579))
        DataHolder.data.addAll(data)
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(data[0].data.toString())).check(matches(ViewMatchers.isDisplayed()))
        onView(withText(data[1].data.toString())).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun dataInput_dateDisplayed() {
        val data = 14579
        val date = System.currentTimeMillis()
        val meterData = MeterData(data, date)
        DataHolder.data.add(meterData)
        launchFragmentInContainer<MeterDataListFragment>(null, R.style.Theme_ElectricityMeter)

        val dateString = dateToString(date)
        onView(withText(dateString)).check(matches(ViewMatchers.isDisplayed()))
    }
}