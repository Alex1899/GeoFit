package com.example.geofitapp.ui.exerciseSetDetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseSetDetails(
    val sets: Int = 0,
    val currentSet: Int = 0,
    val reps: String = "",
    val pace: String = "",
    val exerciseTimeTaken: String = "",
    val angleList: MutableList<Triple<String, MutableList<Double>, Triple<Float, Float, Boolean>>> = mutableListOf(),
    val feedback: MutableMap<Int, MutableMap<String, MutableMap<String, Pair<String, String>>>>
) : Parcelable
