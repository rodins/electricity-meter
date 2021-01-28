package com.sergeyrodin.electricitymeter

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.di.TestModule
import com.sergeyrodin.electricitymeter.utils.DataBindingIdlingResource
import com.sergeyrodin.electricitymeter.utils.EspressoIdlingResource
import com.sergeyrodin.electricitymeter.utils.monitorActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(TestModule::class)
@HiltAndroidTest
class AppNavigationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataSource: MeterDataSource

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
    fun init() {
        hiltRule.inject()
        runBlocking {
            dataSource.deleteAllMeterData()
            dataSource.deleteAllPaidDates()
        }
    }

    @Test
    fun navigateBackFromPaidDatesToMeterDataList() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        pressBack()

        onView(withId(R.id.paidListFragment)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun navigateUpFromPaidDatesToMeterDataList() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())

        onView(withId(R.id.paidListFragment)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}