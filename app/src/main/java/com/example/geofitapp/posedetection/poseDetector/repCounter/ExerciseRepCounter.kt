package com.example.geofitapp.posedetection.poseDetector.repCounter

import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor

abstract class ExerciseRepCounter {
    abstract var overallTotalReps: Int?

    abstract fun init(side: String)
    abstract fun addNewFramePoseAngles(angleMap: MutableMap<Int, Double>, mainAOIindex: Int): ExerciseProcessor
    abstract fun getTotalReps(): Int
    abstract fun resetTotalReps()
}