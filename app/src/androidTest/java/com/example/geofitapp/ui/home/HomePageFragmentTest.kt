package com.example.geofitapp.ui.home

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.geofitapp.R
import com.example.geofitapp.ui.exercisePreview.ExerciseData
import com.example.geofitapp.ui.exercisePreview.ExercisePreviewFragment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class HomePageFragmentTest{
    private lateinit var scenario: FragmentScenario<HomePageFragment>
    private val context = InstrumentationRegistry.getInstrumentation().targetContext


    @Before
    fun init(){
        scenario = launchFragmentInContainer()
    }

    @Test
    fun isInformationVisible(){
        onView(withId(R.id.bicep_curl_cardview)).check(matches(isDisplayed()))
        onView(withId(R.id.triceps_pushdown_cardview)).check(matches(isDisplayed()))
        onView(withId(R.id.shoulder_press_cardview)).check(matches(isDisplayed()))
        onView(withId(R.id.front_raise_cardview)).check(matches(isDisplayed()))

    }

    @Test
    fun isButtonClickable(){
        onView(withId(R.id.bicep_curl_cardview)).check(matches(isClickable()))
        onView(withId(R.id.triceps_pushdown_cardview)).check(matches(isClickable()))
        onView(withId(R.id.shoulder_press_cardview)).check(matches(isClickable()))
        onView(withId(R.id.front_raise_cardview)).check(matches(isClickable()))
    }

    @Test
    fun isExercisePreviewStarted(){
        // Create a mock NavController
        val mockNavController = mock(NavController::class.java)

        // Set the NavController property on the fragment
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        val exerciseData = ExerciseData(
            R.raw.bicep_curl_tut,
            R.drawable.bicep_curl_thumb,
            "Dumbbell Bicep Curl",
            context.getString(R.string.bicep_curl_desc)

        )

        onView(withId(R.id.bicep_curl_cardview)).perform(click())
        verify(mockNavController).navigate(HomePageFragmentDirections.actionHomePageFragmentToExercisePreviewFragment(exerciseData))
    }

}