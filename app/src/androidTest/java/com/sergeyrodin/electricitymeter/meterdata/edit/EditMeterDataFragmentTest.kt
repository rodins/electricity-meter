package com.sergeyrodin.electricitymeter.meterdata.edit

import androidx.appcompat.view.menu.ActionMenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.di.MeterDataSourceModule
import com.sergeyrodin.electricitymeter.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(MeterDataSourceModule::class)
class EditMeterDataFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun meterDataIdArg_meterDataIsDisplayed() {
        val id = 1
        val data = 15058
        dataSource.insertMeterDataBlocking(MeterData(id = id, data = data))

        val args = EditMeterDataFragmentArgs(id).toBundle()
        launchFragmentInHiltContainer<EditMeterDataFragment>(args, R.style.Theme_ElectricityMeter)

        onView(withText(data.toString())).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onDeleteActionClick_navigationCalled() {
        val id = 1
        val data = 15058
        dataSource.insertMeterDataBlocking(MeterData(id = id, data = data))
        val args = EditMeterDataFragmentArgs(id).toBundle()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.addEditMeterDataFragment)

        launchFragmentInHiltContainer<EditMeterDataFragment>(
            args,
            R.style.Theme_ElectricityMeter
        ) {
            val deleteMenuItem = ActionMenuItem(
                ApplicationProvider.getApplicationContext(),
                0,
                R.id.action_delete,
                0,
                0,
                null
            )

            Navigation.setViewNavController(requireView(), navController)
            onOptionsItemSelected(deleteMenuItem)
        }

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }
}