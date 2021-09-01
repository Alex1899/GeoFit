package com.example.geofitapp.ui.exerciseSetDetails

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geofitapp.R
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseSetDetailsActivityTest {
    val str: String? = null
    val mlst: MutableList<Double>? = null
    val flNull: Float? = null
    val fl: Float = 10f
    val allAngles = mutableListOf(
        Triple(
            Pair("Elbow angles", str),
            Pair(
                mutableListOf(166.29090907956592, 30.60079202124913, 168.27598433959753),
                mlst
            ),
            listOf(
                Triple(fl, flNull, false),
                Triple(flNull, fl, false)
            )
        )
    )
    val feedback = mutableMapOf(
        1 to mutableMapOf(
            "Starting Position" to mutableMapOf(
                "Min elbow angle" to Triple(
                    "correct",
                    "Well done",
                    "Nice"
                )
            )
        )
    )
    val testData = ExerciseSetDetails(
        "Dumbbell Bicep Curl",
        3,
        1,
        "1/12",
        "0.0s",
        "10.0s",
        allAngles,
        feedback
    )
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    val intent = Intent(
        context,
        ExerciseSetDetailsActivity::class.java
    ).apply { putExtra("exerciseSetDetails", testData) }


    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<ExerciseSetDetailsActivity>(intent)

    @Test
    fun isGeofitScoreVisible() {
        Espresso.pressBack();
        onView(withId(R.id.overall_feedback)).check(matches(isDisplayed()))
        onView(withId(R.id.mistakes_number)).check(matches(isDisplayed()))
        onView(withId(R.id.exercise_name_feedback)).check(matches(isDisplayed()))
    }

    @Test
    fun isChartDisplayed() {
        Espresso.pressBack();
        onView(withId(R.id.chart_recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun isExerciseInfoShown() {
        Espresso.pressBack();

        onView(withId(R.id.set_info)).check(matches(isDisplayed()))
    }

    @Test
    fun isRestTimerStarted() {
        Espresso.pressBack();

        onView(withId(R.id.progress_countdown)).check(matches(isDisplayed()))
    }

    @Test
    fun isFeedbackGiven() {
        Espresso.pressBack();

        onView(withId(R.id.feedback_recycler_view))
    }
}
