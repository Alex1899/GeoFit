package com.example.geofitapp.ui.exerciseSetDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.geofitapp.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.junit.Test

import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule

class ExerciseSetDetailsViewModelTest {
    private lateinit var setDetailsViewModel: ExerciseSetDetailsViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        setDetailsViewModel = ExerciseSetDetailsViewModel()
    }

    @Test
    fun saveSetDetails() {
        val set = ExerciseSetDetails(feedback = mutableMapOf())
        setDetailsViewModel.saveSetDetails(set)
        val value = setDetailsViewModel.setDetails.getOrAwaitValue()
        assertThat(value, CoreMatchers.not(CoreMatchers.nullValue()))

    }
}