package com.sergeyrodin.electricitymeter.paidlist

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.util.Checks
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.ServiceLocator
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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

    @Test
    fun itemClicked_navigationCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val id = 1
        val date = 1602219377796
        dataSource.testInsert(PaidDate(id, date))
        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withText(dateToString(date))).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataListFragment))
    }

    @Test
    fun itemLongPress_itemDeleted() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withText(dateToString(date1))).perform(longClick())
        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).perform(click())
        onView(withText(dateToString(date1))).check(doesNotExist())
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))
    }

    @Test
    fun itemLongClick_backgroundColorMatches() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .check(
                matches(
                    ViewMatchers.hasDescendant(
                        hasBackgroundColor(R.color.design_default_color_background)
                    )
                )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .check(
                matches(
                    ViewMatchers.hasDescendant(
                        hasBackgroundColor(R.color.design_default_color_secondary)
                    )
                )
            )
    }

    @Test
    fun twoItems_itemLongClick_backgroundColorMatches() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date2)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .check(
                matches(
                    ViewMatchers.hasDescendant(
                        hasBackgroundColorAndText(
                            R.color.design_default_color_secondary,
                            dateToString(date2))
                    )
                )
            )
    }

    @Test
    fun itemLongClick_deleteButtonDisplayed() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).check(matches(isDisplayed()))
    }

    @Test
    fun itemDeleted_deleteButtonNotDisplayed() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withText(dateToString(date1))).perform(longClick())
        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).perform(click())
        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).check(doesNotExist())
    }

    @Test
    fun clickOnHighlightedItem_itemNotHighlighted() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), click()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .check(
                matches(
                    ViewMatchers.hasDescendant(
                        hasBackgroundColor(R.color.design_default_color_background)
                    )
                )
            )
    }

    @Test
    fun clickOnHighlightedItem_deleteButtonNotDisplayed() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), click()
                    )
            )

        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).check(doesNotExist())
    }

    @Test
    fun clickOnNotHighlightedItem_highlightedItemNotHighlighted() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date2)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date1)), click()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .check(
                matches(not(ViewMatchers.hasDescendant(
                    hasBackgroundColorAndText(
                        R.color.design_default_color_secondary,
                        dateToString(date2))
                )))
            )
    }

    @Test
    fun clickOnNotHighlightedItem_deleteButtonNotDisplayed() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date2)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date1)), click()
                    )
            )

        onView(ViewMatchers.withId(R.id.action_delete_paid_date)).check(doesNotExist())
    }

    @Test
    fun actionMode_clickOnHighlightedItem_navigationNotCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        val scenario = launchFragmentInContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date2)), longClick()
                    )
            )

        onView(ViewMatchers.withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date2)), click()
                    )
            )

        assertThat(navController.currentDestination?.id, `is`(R.id.paidListFragment))
    }

    private fun hasBackgroundColor(colorRes: Int): Matcher<View> {
        Checks.checkNotNull(colorRes)
        return object: TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("background color: $colorRes")
            }

            override fun matchesSafely(item: View?): Boolean {
                if(item?.background == null) {
                    return false
                }
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor = ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color
                return actualColor == expectedColor
            }

        }
    }

    private fun hasBackgroundColorAndText(colorRes: Int, text: String): Matcher<View> {
        return object: TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("text: $text, background color: $colorRes")
            }

            override fun matchesSafely(item: View?): Boolean {
                if(item?.background == null)
                    return false
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor = ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color

                val dateTextView = item as TextView
                val actualText = dateTextView.text.toString()
                return actualColor == expectedColor && text == actualText
            }
        }
    }
}