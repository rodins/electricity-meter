package com.sergeyrodin.electricitymeter.meterdata.edit

import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.MeterData
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class AddEditMeterDataFragmentTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var dataSource: FakeDataSource
    private lateinit var noMeterDataIdArgs: Bundle

    @Before
    fun initDataSource() {
        dataSource = FakeDataSource()
        ServiceLocator.dataSource = dataSource
        noMeterDataIdArgs = AddEditMeterDataFragmentArgs().toBundle()
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
        navController.setCurrentDestination(R.id.addEditMeterDataFragment)

        val scenario = launchFragmentInContainer<AddEditMeterDataFragment>(
            noMeterDataIdArgs,
            R.style.Theme_ElectricityMeter
        )
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.meter_data_edit)).perform(typeText(data.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }

    @Test
    fun saveMeterData_meterDataEquals() {
        val data = 15058
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.addEditMeterDataFragment)

        val scenario = launchFragmentInContainer<AddEditMeterDataFragment>(
            noMeterDataIdArgs,
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

    @Test
    fun meterDataIdArg_meterDataIsDisplayed() {
        val id = 1
        val data = 15058
        dataSource.testInsert(MeterData(id = id, data = data))

        val args = AddEditMeterDataFragmentArgs(id).toBundle()
        launchFragmentInContainer<AddEditMeterDataFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withText(data.toString())).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onDeleteActionClick_navigationCalled() {
        val id = 1
        val data = 15058
        dataSource.testInsert(MeterData(id = id, data = data))
        val args = AddEditMeterDataFragmentArgs(id).toBundle()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.addEditMeterDataFragment)

        val scenario = launchFragmentInContainer<AddEditMeterDataFragment>(
            args,
            R.style.Theme_ElectricityMeter
        )
        val deleteMenuItem = ActionMenuItem(
            ApplicationProvider.getApplicationContext(),
            0,
            R.id.action_delete,
            0,
            0,
            null
        )
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
            fragment.onOptionsItemSelected(deleteMenuItem)
        }

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }
}