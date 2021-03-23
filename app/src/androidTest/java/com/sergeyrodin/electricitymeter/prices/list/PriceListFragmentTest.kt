package com.sergeyrodin.electricitymeter.prices.list

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.sergeyrodin.electricitymeter.*
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.di.MeterDataSourceModule
import com.sergeyrodin.electricitymeter.utils.hasBackgroundColor
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PriceListFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var dataSource: FakeDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun addPriceFabClick_navigationCalled() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.navigation)
        navController.setCurrentDestination(R.id.priceListFragment)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.add_price_fab)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.priceFragment))
    }

    @Test
    fun insertPrice_priceIsDisplayed() {
        dataSource.insertPriceBlocking(Price(1, 1.68))

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText("1.68")).check(matches(isDisplayed()))
    }

    @Test
    fun insertTwoPrices_pricesIsDisplayed() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price1.price.toString())).check(matches(isDisplayed()))
        onView(withText(price2.price.toString())).check(matches(isDisplayed()))
    }

    @Test
    fun insertPrice_deletePrice_priceCountZero() {
        dataSource.insertPriceBlocking(Price(1, 1.68))

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText("1.68")).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        val count = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(count, `is`(0))
    }

    @Test
    fun priceLongClick_actionDeleteIsDisplayed() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)
        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())

        onView(withId(R.id.action_delete_price)).check(matches(isDisplayed()))
    }

    @Test
    fun insertTwoPrices_deletePrice_priceCountEquals() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price2.price.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        val count = dataSource.getObservablePriceCount().getOrAwaitValue()
        assertThat(count, `is`(1))
    }

    @Test
    fun twoPrices_deletePrice_actionDeleteNotDisplayed() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price2.price.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        onView(withId(R.id.action_delete_price)).check(matches(not(isDisplayed())))
    }

    @Test
    fun insertPrice_noLongClickOnPrice_actionDeleteNotDisplayed() {
        dataSource.insertPriceBlocking(Price(1, 1.68))

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withId(R.id.action_delete_price)).check(doesNotExist())
    }

    @Test
    fun priceLongClick_priceHighlighted() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())

        onView(withId(R.id.price_items)).check(matches(hasDescendant(
            hasBackgroundColor(R.color.design_default_color_secondary))))
    }

    @Test
    fun testDefaultBackgroundColor() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withId(R.id.price_items)).check(matches(hasDescendant(
            hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun clickOnHighlightedPrice_priceNotHighlighted() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())
        onView(withText(price.price.toString())).perform(click())

        onView(withId(R.id.price_items)).check(matches(hasDescendant(
            hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun clickOnNotHighlightedPrice_priceNotHighlighted() {
        val price1 = Price(1, 1.68)
        val price2 = Price(2, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price1.price.toString())).perform(longClick())
        onView(withText(price2.price.toString())).perform(click())

        onView(withId(R.id.price_items)).check(matches(hasDescendant(withColorAndText(
            R.color.design_default_color_background,
            price1.price.toString()))))
    }

    private fun withColorAndText(colorRes: Int, text: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description?) {
                description?.appendText("with color: $colorRes and text: $text")
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item?.background == null)
                    return false
                val actualColor = (item.background as ColorDrawable).color
                val expectedColor =
                    ColorDrawable(ContextCompat.getColor(item.context, colorRes)).color

                val textView = item as TextView
                val actualText = textView.text.toString()
                return actualColor == expectedColor && text == actualText
            }
        }
    }

    @Test
    fun clickOnHighlightedPrice_actionDeleteNotDisplayed() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())
        onView(withText(price.price.toString())).perform(click())

        onView(withId(R.id.action_delete_price)).check(matches(not(isDisplayed())))
    }

    @Test
    fun highlightedPrice_pressBack_priceNotHighlighted() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())

        Espresso.pressBack()

        onView(withId(R.id.price_items)).check(matches(hasDescendant(
            hasBackgroundColor(R.color.design_default_color_background))))
    }

    @Test
    fun noPrices_noPricesTextDisplayed() {
        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(R.string.no_prices)).check(matches(isDisplayed()))
    }

    @Test
    fun threePrices_onePriceDeleted_lastPriceHighlighted() {
        val price1 = Price(1, 0.9)
        val price2 = Price(2, 1.68)
        val price3 = Price(3, 2.0)
        dataSource.insertPriceBlocking(price1)
        dataSource.insertPriceBlocking(price2)
        dataSource.insertPriceBlocking(price3)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price2.price.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        onView(withText(price3.price.toString())).perform(longClick())

        onView(withId(R.id.price_items)).check(matches(hasDescendant(withColorAndText(
            R.color.design_default_color_secondary,
            price3.price.toString()))))
    }

    @Test
    fun paidDateConstraint_deletePrice_deleteErrorDisplayed() {
        val price = Price(1, 1.68)
        dataSource.insertPriceBlocking(price)
        val paidDate = PaidDate(1, System.currentTimeMillis(), price.id)
        dataSource.insertPaidDateBlocking(paidDate)

        launchFragmentInHiltContainer<PriceListFragment>(null,  R.style.Theme_ElectricityMeter)

        onView(withText(price.price.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        val resources = ApplicationProvider.getApplicationContext<HiltTestApplication>().resources
        val error = resources.getQuantityString(R.plurals.delete_price_error, 1, 1)
        onView(withText(error)).check(matches(isDisplayed()))
    }

}