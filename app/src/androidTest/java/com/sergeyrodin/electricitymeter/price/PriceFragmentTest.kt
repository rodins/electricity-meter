package com.sergeyrodin.electricitymeter.price

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.Price
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
class PriceFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initHiltRule() {
        hiltRule.inject()
    }

    @Test
    fun saveFabClick_navigationCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.priceFragment)

        launchFragmentInHiltContainer<PriceFragment>(null,  R.style.Theme_ElectricityMeter) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.price_edit)).perform(typeText("1.68"))
        onView(withId(R.id.save_price_fab)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }

    @Test
    fun priceSet_priceDisplayed() {
        val price = 1.68
        dataSource.insertPriceBlocking(Price(1, price))

        launchFragmentInHiltContainer<PriceFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.toString())).check(matches(isDisplayed()))
    }
}