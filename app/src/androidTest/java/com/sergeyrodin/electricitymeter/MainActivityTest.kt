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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

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
}