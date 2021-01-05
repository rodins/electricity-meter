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
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class AddMeterDataFragmentTest {
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
    fun saveMeterData_navigationCalled() {
        val data = 15058
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.addMeterDataFragment)

        val scenario = launchFragmentInContainer<AddMeterDataFragment>(
            null,
            R.style.Theme_ElectricityMeter
        )
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.meter_data_edit)).perform(typeText(data.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment)
        )
    }

    @Test
    fun saveMeterData_meterDataEquals() {
        val data = 15058
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.addMeterDataFragment)

        val scenario = launchFragmentInContainer<AddMeterDataFragment>(
            null,
            R.style.Theme_ElectricityMeter
        )
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.meter_data_edit)).perform(typeText(data.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        val items = dataSource.getMeterDataForTest()
        assertThat(items[0].data, `is`(data))
    }
}