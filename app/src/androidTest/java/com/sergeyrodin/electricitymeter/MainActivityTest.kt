package com.sergeyrodin.electricitymeter

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.MeterData
import com.sergeyrodin.electricitymeter.database.PaidDate
import com.sergeyrodin.electricitymeter.datasource.MeterDataSource
import com.sergeyrodin.electricitymeter.meterdata.dateToString
import com.sergeyrodin.electricitymeter.utils.DataBindingIdlingResource
import com.sergeyrodin.electricitymeter.utils.EspressoIdlingResource
import com.sergeyrodin.electricitymeter.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    private lateinit var dataSource: MeterDataSource

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
    fun initDataSource() {
        dataSource = ServiceLocator.provideMeterDataSource(getApplicationContext())
        runBlocking {
            dataSource.deleteAllMeterData()
            dataSource.deleteAllPaidDates()
        }
    }

    @After
    fun clearDatabase() {
        ServiceLocator.resetDataSource()
    }

    @Test
    fun onHistoryClick_noDataDisplayed() = runBlocking {
        val data = 14622
        dataSource.insert(MeterData(data))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

        val paidDate1 = PaidDate(date = date2)
        val paidDate2 = PaidDate(date = date4)

        dataSource.insertPaidDate(paidDate1)
        dataSource.insertPaidDate(paidDate2)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data4.toString())).check(matches(isDisplayed()))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

        val paidDate = PaidDate(date = date2)

        dataSource.insertPaidDate(paidDate)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withSubstring(data1.toString())).check(doesNotExist())
        onView(withSubstring(data2.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data3.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data4.toString())).check(matches(isDisplayed()))
        onView(withSubstring(data5.toString())).check(matches(isDisplayed()))

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
        dataSource.insert(MeterData(data1))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

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
        dataSource.insert(MeterData(data))

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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
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
        dataSource.insert(MeterData(data1, date = date1))
        dataSource.insert(MeterData(data2, date = date2))
        dataSource.insert(MeterData(data3, date = date3))
        dataSource.insert(MeterData(data4, date = date4))
        dataSource.insert(MeterData(data5))

        val paidDate = PaidDate(date = date2)

        dataSource.insertPaidDate(paidDate)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.paidListFragment)).perform(click())
        onView(withText(dateToString(date2))).perform(click())

        onView(withId(R.id.action_paid)).check(doesNotExist())

        activityScenario.close()
    }

}