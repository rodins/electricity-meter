package com.sergeyrodin.electricitymeter.paidlist

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.internal.util.Checks
import com.sergeyrodin.electricitymeter.FakeDataSource
import com.sergeyrodin.electricitymeter.R
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.di.MeterDataSourceModule
import com.sergeyrodin.electricitymeter.launchFragmentInHiltContainer
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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
class PaidListFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun initDataSource() {
        hiltRule.inject()
    }

    @Test
    fun noItems_noDataTextDisplayed() {
        launchFragmentInHiltContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))
    }

    @Test
    fun oneItem_noDataTextNotDisplayed() {
        val date = 1602219377796L
        dataSource.testInsert(PaidDate(date = date))
        launchFragmentInHiltContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(R.string.no_items)).check(matches(not(isDisplayed())))
    }

    @Test
    fun oneItem_dateDisplayed() {
        val date = 1602219377796L
        dataSource.testInsert(PaidDate(date = date))
        launchFragmentInHiltContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(dateToString(date))).check(matches(isDisplayed()))
    }

    @Test
    fun twoItems_datesDisplayed() {
        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        launchFragmentInHiltContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter)

        onView(withText(dateToString(date1))).check(matches(isDisplayed()))
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))
    }

    @Test
    fun itemClicked_navigationCalled() {
        val navController = testNavHostController()

        val id = 1
        val date = 1602219377796
        dataSource.testInsert(PaidDate(id, date))
        launchFragmentInHiltContainer<PaidListFragment>(null, R.style.Theme_ElectricityMeter) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withText(dateToString(date))).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.meterDataHistoryFragment))
    }

    @Test
    fun itemLongPress_itemDeleted() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withText(dateToString(date1))).perform(longClick())
        clickDeleteButton()
        onView(withText(dateToString(date1))).check(doesNotExist())
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))
    }

    @Test
    fun itemLongClick_backgroundColorMatches() {
        val navController = testNavHostController()

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        listHasNotHighlightedItem()
        longClickOnDate(date)

        listHasHighlightedItem()
    }

    private fun listHasHighlightedItem() {
        onView(withId(R.id.date_items))
            .check(
                matches(
                    hasDescendant(
                        hasBackgroundColor(R.color.design_default_color_secondary)
                    )
                )
            )
    }

    @Test
    fun twoItems_itemLongClick_backgroundColorMatches() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date2)

        dateIsHighlighted(date2)
    }

    private fun dateIsHighlighted(date: Long) {
        onView(withId(R.id.date_items))
            .check(
                matches(
                    hasDescendant(
                        hasBackgroundColorAndText(
                            R.color.design_default_color_secondary,
                            dateToString(date)
                        )
                    )
                )
            )
    }

    @Test
    fun itemLongClick_deleteButtonDisplayed() {
        val navController = testNavHostController()

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date)

        onView(withId(R.id.action_delete_paid_date)).check(matches(isDisplayed()))
    }

    @Test
    fun itemDeleted_deleteButtonNotDisplayed() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withText(dateToString(date1))).perform(longClick())
        clickDeleteButton()
        onView(withId(R.id.action_delete_paid_date)).check(matches(not(isDisplayed())))
    }

    private fun clickDeleteButton() {
        onView(withId(R.id.action_delete_paid_date)).perform(click())
    }

    @Test
    fun clickOnHighlightedItem_itemNotHighlighted() {
        val navController = testNavHostController()

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date)

        clickOnDate(date)

        listHasNotHighlightedItem()
    }

    private fun listHasNotHighlightedItem() {
        onView(withId(R.id.date_items))
            .check(
                matches(
                    hasDescendant(
                        hasBackgroundColor(R.color.design_default_color_background)
                    )
                )
            )
    }

    @Test
    fun clickOnHighlightedItem_deleteButtonNotDisplayed() {
        val navController = testNavHostController()

        val date = 1602219377796
        dataSource.testInsert(PaidDate(date = date))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date)

        clickOnDate(date)

        onView(withId(R.id.action_delete_paid_date)).check(matches(not(isDisplayed())))
    }

    @Test
    fun clickOnNotHighlightedItem_highlightedItemNotHighlighted() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date2)

        clickOnDate(date1)

        dateIsNotHighlighted(date2)
    }

    private fun dateIsNotHighlighted(date: Long) {
        onView(withId(R.id.date_items))
            .check(
                matches(
                    not(
                        hasDescendant(
                            hasBackgroundColorAndText(
                                R.color.design_default_color_secondary,
                                dateToString(date)
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun clickOnNotHighlightedItem_deleteButtonNotDisplayed() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date2)

        clickOnDate(date1)

        onView(withId(R.id.action_delete_paid_date)).check(matches(not(isDisplayed())))
    }

    @Test
    fun actionMode_clickOnHighlightedItem_navigationNotCalled() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date2)

        clickOnDate(date2)

        assertThat(navController.currentDestination?.id, `is`(R.id.paidListFragment))
    }

    @Test
    fun dateDeleted_dateHighlightedEquals() {
        val navController = testNavHostController()

        val date1 = 1602219377796
        val date2 = 1604123777809
        val date3 = 1606715777809
        val date4 = System.currentTimeMillis()

        dataSource.testInsert(PaidDate(date = date1))
        dataSource.testInsert(PaidDate(date = date2))
        dataSource.testInsert(PaidDate(date = date3))
        dataSource.testInsert(PaidDate(date = date4))

        launchFragmentInHiltContainer<PaidListFragment>(
            null, R.style.Theme_ElectricityMeter
        ) {
            Navigation.setViewNavController(requireView(), navController)
        }

        longClickOnDate(date1)
        clickDeleteButton()

        longClickOnDate(date2)
        dateIsHighlighted(date2)
    }

    private fun clickOnDate(date: Long) {
        onView(withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), click()
                    )
            )
    }

    private fun longClickOnDate(date: Long) {
        onView(withId(R.id.date_items))
            .perform(
                RecyclerViewActions
                    .actionOnItem<PaidListAdapter.ViewHolder>(
                        withText(dateToString(date)), longClick()
                    )
            )
    }

    private fun testNavHostController(): TestNavHostController {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.paidListFragment)
        return navController
    }

    private fun hasBackgroundColor(colorRes: Int): Matcher<View> {
        Checks.checkNotNull(colorRes)
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("background color: $colorRes")
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item?.background == null) {
                    return false
                }
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor =
                    ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color
                return actualColor == expectedColor
            }

        }
    }

    private fun hasBackgroundColorAndText(colorRes: Int, text: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("text: $text, background color: $colorRes")
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item?.background == null)
                    return false
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor =
                    ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color

                val dateTextView = item as TextView
                val actualText = dateTextView.text.toString()
                return actualColor == expectedColor && text == actualText
            }
        }
    }
}