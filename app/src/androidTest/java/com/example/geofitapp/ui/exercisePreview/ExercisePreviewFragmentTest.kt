package com.example.geofitapp.ui.exercisePreview

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geofitapp.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExercisePreviewFragmentTest {
    private lateinit var scenario: FragmentScenario<ExercisePreviewFragment>
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testData = ExerciseData(
            R.raw.bicep_curl_tut,
            R.drawable.bicep_curl_thumb,
        "Dumbbell Bicep Curl",
            context.getString(R.string.bicep_curl_desc)

    )

    @Before
    fun init(){
        scenario = launchFragmentInContainer(bundleOf("exerciseData" to testData))
    }

    @Test
    fun isInformationDisplayed() {
        onView(withId(R.id.reps_editText)).check(matches(withText("12")))
        onView(withId(R.id.sets_editText)).check(matches(withText("3")))
        onView(withId(R.id.rest_editText)).check(matches(withText("60")))
        onView(withId(R.id.weight_editText)).check(matches(withText("0.0")))
    }

    @Test
    fun isTextChanged_sameFragment(){
        onView(withId(R.id.reps_editText)).perform(clearText())
        onView(withId(R.id.reps_editText)).perform(typeText("5"), closeSoftKeyboard())
        onView(withId(R.id.reps_editText)).check(matches(withText("5")))

        onView(withId(R.id.sets_editText)).perform(clearText())
        onView(withId(R.id.sets_editText)).perform(typeText("5"), closeSoftKeyboard())
        onView(withId(R.id.sets_editText)).check(matches(withText("5")))

        onView(withId(R.id.rest_editText)).perform(clearText())
        onView(withId(R.id.rest_editText)).perform(typeText("5"), closeSoftKeyboard())
        onView(withId(R.id.rest_editText)).check(matches(withText("5")))

        onView(withId(R.id.weight_editText)).perform(clearText())
        onView(withId(R.id.weight_editText)).perform(typeText("5.0"), closeSoftKeyboard())
        onView(withId(R.id.weight_editText)).check(matches(withText("5.0")))
    }

    @Test
    fun isTextChanged_newActivity(){
        onView(withId(R.id.reps_editText)).perform(clearText())
        onView(withId(R.id.reps_editText)).perform(typeText("5"), closeSoftKeyboard())

        onView(withId(R.id.sets_editText)).perform(clearText())
        onView(withId(R.id.sets_editText)).perform(typeText("5"), closeSoftKeyboard())

        onView(withId(R.id.start_exercise)).perform(click())

        // new activity
        onView(withId(R.id.testRep)).check(matches(withText("/5")))
        onView(withId(R.id.testSet)).check(matches(withText("/5")))


    }


    @Test
    fun isButtonClickable(){
        onView(withId(R.id.reps_plus_icon)).check(matches(isClickable()))
        onView(withId(R.id.reps_minus_icon)).check(matches(isClickable()))

        onView(withId(R.id.sets_plus_icon)).check(matches(isClickable()))
        onView(withId(R.id.sets_plus_icon)).check(matches(isClickable()))

        onView(withId(R.id.rest_plus_icon)).check(matches(isClickable()))
        onView(withId(R.id.rest_plus_icon)).check(matches(isClickable()))

        onView(withId(R.id.weight_plus_icon)).check(matches(isClickable()))
        onView(withId(R.id.weight_plus_icon)).check(matches(isClickable()))
    }

    @Test
    fun isButtonFunctional(){
        onView(withId(R.id.reps_plus_icon)).perform(click())
        // initially its 12
        onView(withId(R.id.reps_editText)).check(matches(withText("13")))
        onView(withId(R.id.reps_minus_icon)).perform(click())
        onView(withId(R.id.reps_editText)).check(matches(withText("12")))

        onView(withId(R.id.sets_plus_icon)).perform(click())
        // initially its 3
        onView(withId(R.id.sets_editText)).check(matches(withText("4")))
        onView(withId(R.id.sets_minus_icon)).perform(click())
        onView(withId(R.id.sets_editText)).check(matches(withText("3")))

        onView(withId(R.id.rest_plus_icon)).perform(scrollTo(), click())
        // initially its 60
        onView(withId(R.id.rest_editText)).check(matches(withText("61")))
        onView(withId(R.id.rest_minus_icon)).perform(scrollTo(), click())
        onView(withId(R.id.rest_editText)).check(matches(withText("60")))

        onView(withId(R.id.weight_plus_icon)).perform(scrollTo(), click())
        // initially its 0.0
        onView(withId(R.id.weight_editText)).check(matches(withText("1.0")))
        onView(withId(R.id.weight_minus_icon)).perform(scrollTo(), click())
        onView(withId(R.id.weight_editText)).check(matches(withText("0.0")))

    }


}