package com.sergeyrodin.electricitymeter

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sergeyrodin.electricitymeter.database.DataHolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @After
    fun clearData() {
        DataHolder.data.clear()
    }

    @Test
    fun addItemClick_nameEquals() {
        val data = "14525"
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_data_fab)).perform(click())
        onView(withId(R.id.data_edit)).perform(typeText(data))
        onView(withId(R.id.save_data_fab)).perform(click())

        onView(withId(R.id.data_edit)).check(doesNotExist())
        onView(withText(data)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addTwoItems_namesDisplayed() {
        val data1 = "14556"
        val data2 = "14579"

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_data_fab)).perform(click())
        onView(withId(R.id.data_edit)).perform(typeText(data1))
        onView(withId(R.id.save_data_fab)).perform(click())

        onView(withId(R.id.add_data_fab)).perform(click())
        onView(withId(R.id.data_edit)).perform(typeText(data2))
        onView(withId(R.id.save_data_fab)).perform(click())

        onView(withText(data1)).check(matches(isDisplayed()))
        onView(withText(data2)).check(matches(isDisplayed()))

        activityScenario.close()
    }
}