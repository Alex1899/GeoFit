package com.example.geofitapp.ui.cameraPreview

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geofitapp.R
import com.example.geofitapp.ui.exerciseSetDetails.ExerciseSetDetailsActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraXLivePreviewActivityTest {

    val bundle = bundleOf(
        "exerciseName" to "Dumbbell Bicep Curl",
        "reps" to "10",
        "sets" to "3",
        "currentSet" to "2"
    )
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    val intent = Intent(context, CameraXLivePreviewActivity::class.java).apply {
        putExtra(
            "exercise",
            bundle
        )
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<ExerciseSetDetailsActivity>(intent)


    @Test
    fun isPageVisible() {
        onView(withId(R.id.preview_view)).check(matches(isDisplayed()))
    }

    @Test
    fun isRecordButtonFunctional() {
        onView(withId(R.id.record_icon)).check(matches(isDisplayed()))
    }

    @Test
    fun isCameraReverseButtonFunctional() {
        onView(withId(R.id.switch_camera)).check(matches(isDisplayed()))
    }

    @Test
    fun isFlashIconFunctional() {
        onView(withId(R.id.flash_icon)).check(matches(isDisplayed()))
    }

    @Test
    fun isTimerIconFunctional() {
        onView(withId(R.id.timer_icon)).check(matches(isDisplayed()))
    }
//
//    @Test
//    fun isRepCounterUpdated() {
//        onView(withId(R.id.reps_overlay_text)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun isSetCountUpdated() {
//        onView(withId(R.id.sets_overlay_text)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun isPaceTimeUpdated() {
//        onView(withId(R.id.pace_overlay_text)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun isErrorCountUpdated() {
//        onView(withId(R.id.errors_overlay_text)).check(matches(isDisplayed()))
//    }
}