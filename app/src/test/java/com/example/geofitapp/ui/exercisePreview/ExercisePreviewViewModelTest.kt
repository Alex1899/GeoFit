package com.example.geofitapp.ui.exercisePreview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.geofitapp.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class ExercisePreviewViewModelTest{

    private lateinit var exercisePreviewViewModel: ExercisePreviewViewModel

    // Executes each task synchronously using Architecture Components.

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        exercisePreviewViewModel = ExercisePreviewViewModel()
    }

    @Test
    fun updateReps_setNewRepCount(){
        exercisePreviewViewModel.updateReps("5")
        val value = exercisePreviewViewModel.reps.getOrAwaitValue()

        MatcherAssert.assertThat(
            value,
            `is`(5)
        )
    }

    @Test
    fun updateSets_setNewSetCount(){
        exercisePreviewViewModel.updateSets("5")
        val value = exercisePreviewViewModel.sets.getOrAwaitValue()

        MatcherAssert.assertThat(
            value,
            `is`(5)
        )
    }

    @Test
    fun updateRests_setNewRestCount(){
        exercisePreviewViewModel.updateRest("50")
        val value = exercisePreviewViewModel.rest.getOrAwaitValue()

        MatcherAssert.assertThat(
            value,
            `is`(50)
        )
    }

    @Test
    fun updateWeight_setNewWeight(){
        exercisePreviewViewModel.updateWeight("5")
        val value = exercisePreviewViewModel.weight.getOrAwaitValue()

        MatcherAssert.assertThat(
            value,
            `is`(5f)
        )
    }

}