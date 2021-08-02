package com.example.geofitapp.ui.exerciseSetDetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseSetDetails(
    val sets: String = "",
    val reps: String = "",
    val pace: String = "",
    val exerciseTimeTaken: String = "",
    val angleList: MutableList<Double> = mutableListOf()
) : Parcelable
