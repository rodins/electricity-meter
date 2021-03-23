package com.sergeyrodin.electricitymeter

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.database.Price
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.di.TestModule
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import com.sergeyrodin.electricitymeter.utils.DataBindingIdlingResource
import com.sergeyrodin.electricitymeter.utils.EspressoIdlingResource
import com.sergeyrodin.electricitymeter.utils.dateToLong
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
import java.util.*
import javax.inject.Inject

private val PRICE = Price(1, price = 1.68)

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(TestModule::class)
@HiltAndroidTest
class MainActivityTest {

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
            dataSource.deletePrices()
            dataSource.insertPrice(PRICE)
        }
    }

    @Test
    fun onHistoryClick_noDataDisplayed() = runBlocking {
        val data = 14622
        dataSource.insertMeterData(MeterData(data))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(R.string.no_items)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun paidClicked_dateEquals() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun twoDatesPaid_datesEqual() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        val data3 = 14638
        val date3 = System.currentTimeMillis()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.add_meter_data_fab)).perform(click())
        onView(withId(R.id.meter_data_edit)).perform(typeText(data3.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())

        onView(withText(dateToString(date2))).check(matches(isDisplayed()))
        onView(withText(dateToString(date3))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun displayMeterDataByPaidDate() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))
        dataSource.insertMeterData(MeterData(data5))

        val paidDate1 = PaidDate(date = date2, priceId = PRICE.id)
        val paidDate2 = PaidDate(date = date4, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(doesNotExist())
        onView(withSubstring(data4.toString())).check(doesNotExist())
        onView(withSubstring(data5.toString())).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun displayMeterDataBySinglePaidDate() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))
        dataSource.insertMeterData(MeterData(data5))

        val paidDate = PaidDate(date = date2, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(doesNotExist())
        onView(withSubstring(data4.toString())).check(doesNotExist())
        onView(withSubstring(data5.toString())).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun addMeterData_meterDataDisplayed() {
        val data = 14314
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_meter_data_fab)).perform(click())
        onView(withId(R.id.meter_data_edit)).perform(typeText(data.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data.toString()))))

        activityScenario.close()
    }

    @Test
    fun addTwoItems_namesDisplayed() {
        val data1 = "14556"
        val data2 = "14579"

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_meter_data_fab)).perform(click())
        onView(withId(R.id.meter_data_edit)).perform(typeText(data1))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        onView(withId(R.id.add_meter_data_fab)).perform(click())
        onView(withId(R.id.meter_data_edit)).perform(typeText(data2))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data1))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data2))))

        activityScenario.close()
    }

    @Test
    fun meterDataItemClick_editData_dataDisplayed() = runBlocking {
        val data1 = 14314
        val data2 = 14556
        dataSource.insertMeterData(MeterData(data1))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withSubstring(data1.toString())).perform(click())
        onView(withId(R.id.meter_data_edit)).perform(replaceText(data2.toString()))
        onView(withId(R.id.save_meter_data_fab)).perform(click())

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data2.toString()))))

        activityScenario.close()
    }

    @Test
    fun selectItem_deleteItem_itemNotDisplayed() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))
        dataSource.insertMeterData(MeterData(data5))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withSubstring(data3.toString())).perform(click())
        onView(withId(R.id.action_delete)).perform(click())

        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data1.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data2.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data4.toString()))))
        onView(withId(R.id.data_list)).check(matches(hasDescendant(withSubstring(data5.toString()))))
        onView(withSubstring(data3.toString())).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun addNewItem_deleteMenuItemNotDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_meter_data_fab)).perform(click())

        onView(withId(R.id.action_delete)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun addNewItem_titleEquals() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.add_meter_data_fab)).perform(click())

        onView(withText(R.string.add_data)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun editItem_titleEquals() = runBlocking {
        val data = 14314
        dataSource.insertMeterData(MeterData(data))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withSubstring(data.toString())).perform(click())

        onView(withText(R.string.edit_data)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun noItems_paidButtonIsNotDisplayed() = runBlocking {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun twoItems_paidButtonIsDisplayed() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun paidClicked_paidNotVisible() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())

        onView(withId(R.id.action_paid)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun navigateFromPaidDates_paidButtonNotDisplayed() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))
        dataSource.insertMeterData(MeterData(data5))

        val paidDate = PaidDate(date = date2, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withId(R.id.action_paid)).check(doesNotExist())

        activityScenario.close()
    }

    @Test
    fun meterDataHistory_titleEquals() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        val data5 = 14971
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))
        dataSource.insertMeterData(MeterData(data5))

        val paidDate = PaidDate(date = date2, priceId = PRICE.id)

        dataSource.insertPaidDate(paidDate)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withText(R.string.meter_data_history)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun displayMeterDataAfterPaidDateDeleted() = runBlocking {
        val data1 = 14314
        val date1 = 1602219377796
        val data2 = 14509
        val date2 = 1604123777809
        val data3 = 14579
        val date3 = 1606715777809
        val data4 = 14638
        val date4 = 1606802177809
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))
        dataSource.insertMeterData(MeterData(data3, date = date3))
        dataSource.insertMeterData(MeterData(data4, date = date4))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date4))).perform(longClick())
        onView(withId(R.id.action_delete_paid_date)).perform(click())

        Espresso.pressBack()

        onView(withSubstring(dateToString(date1))).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun firstMonthPaidDateClick_historyDisplayed() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 30, 9, 0)
        val data2 = 15123
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun displayPriceInPaidDate() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 30, 9, 0)
        val data2 = 15123
        val price = 703.92
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())
        onView(withId(R.id.paidListFragment)).perform(click())

        onView(withText(price.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun pricesList_addPrice_totalPriceEquals() = runBlocking {
        dataSource.deletePrices()
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714
        val totalPrice = 16.8
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.set_price_button)).perform(click())
        onView(withId(R.id.add_price_fab)).perform(click())
        onView(withId(R.id.price_edit)).perform(typeText("1.68"))
        onView(withId(R.id.save_price_fab)).perform(click())

        Espresso.pressBack()

        onView(withSubstring(totalPrice.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun pricesList_updatePrice_totalPriceEquals() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714
        val totalPrice = 20.0
        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.priceListFragment)).perform(click())
        onView(withId(R.id.add_price_fab)).perform(click())
        onView(withId(R.id.price_edit)).perform(replaceText("2.00"))
        onView(withId(R.id.save_price_fab)).perform(click())

        Espresso.pressBack()

        onView(withSubstring(totalPrice.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun onePrice_deletePrice_setPriceButtonIsDisplayed() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.priceListFragment)).perform(click())
        onView(withText("1.68")).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        Espresso.pressBack()

        onView(withId(R.id.set_price_button)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addSecondPrice_deleteSecondPrice_totalPriceEquals() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714
        val price2 = 2.0

        val totalPrice = 16.8

        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.priceListFragment)).perform(click())
        onView(withId(R.id.add_price_fab)).perform(click())
        onView(withId(R.id.price_edit)).perform(replaceText(price2.toString()))
        onView(withId(R.id.save_price_fab)).perform(click())

        onView(withText(price2.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        Espresso.pressBack()

        onView(withSubstring(totalPrice.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun priceInserted_paidButtonClicked_priceUpdated_firstHistoryPriceDisplayed() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        val historyTotalPrice = 16.8

        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())

        onView(withId(R.id.priceListFragment)).perform(click())
        onView(withId(R.id.add_price_fab)).perform(click())
        onView(withId(R.id.price_edit)).perform(replaceText("2.00"))
        onView(withId(R.id.save_price_fab)).perform(click())

        Espresso.pressBack()

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(historyTotalPrice.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun paidDateConstraint_priceNotDeleted_historyTotalPriceEquals() = runBlocking {
        val date1 = dateToLong(2020, Calendar.DECEMBER, 1, 9, 0)
        val data1 = 14704
        val date2 = dateToLong(2020, Calendar.DECEMBER, 3, 9, 0)
        val data2 = 14714

        val historyTotalPrice = 16.8

        dataSource.insertMeterData(MeterData(data1, date = date1))
        dataSource.insertMeterData(MeterData(data2, date = date2))

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.action_paid)).perform(click())

        onView(withId(R.id.priceListFragment)).perform(click())

        onView(withText(PRICE.price.toString())).perform(longClick())
        onView(withId(R.id.action_delete_price)).perform(click())

        Espresso.pressBack()

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(historyTotalPrice.toString())).check(matches(isDisplayed()))

        activityScenario.close()
    }

}