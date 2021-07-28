package com.example.geofitapp.ui.exercisePreview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseData(
    val video: Int = 0,
    val videoThumbnail: Int = 0,
    val exerciseName: String = "",
    val description: String = ""
) : Parcelable
