package com.sergeyrodin.electricitymeter.meterdata.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.DataHolder
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MeterDataInputFragmentTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun saveDataClick_navigationCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.meterDataInputFragment)

        val scenario = launchFragmentInContainer<MeterDataInputFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.save_data_fab)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }

    @Test
    fun saveDataClick_dataEquals() {
        val data = "14579"
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.meterDataInputFragment)

        val scenario = launchFragmentInContainer<MeterDataInputFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.save_data_fab)).perform(click())

        val saved = DataHolder.data
        assertThat(saved, `is`(data))
    }
}