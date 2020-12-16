package com.sergeyrodin.electricitymeter.paidlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class PaidListFragmentTest {
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
    fun noItems_noDataTextDisplayed() {
        launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))
    }

    @Test
    fun oneItem_noDataTextNotDisplayed() {
        val date = 1602219377796L
        dataSource.testInsert(PaidDate(date = date))
        launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(R.string.no_items)).check(matches(not(isDisplayed())))
    }

    @Test
    fun oneItem_dateDisplayed() {
        val date = 1602219377796L
        dataSource.testInsert(PaidDate(date = date))
        launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(dateToString(date))).check(matches(isDisplayed()))
    }

    @Test
    fun twoItems_datesDisplayed() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(dateToString(date1))).check(matches(isDisplayed()))
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))
    }
}