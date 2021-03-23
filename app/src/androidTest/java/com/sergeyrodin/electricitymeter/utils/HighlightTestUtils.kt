package com.sergeyrodin.electricitymeter.utils

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.test.internal.util.Checks
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun hasBackgroundColor(colorRes: Int): Matcher<View> {
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