package com.example.geofitapp.posedetection.poseDetector.repCounter

abstract class ExerciseRepCounter {
    abstract fun addNewFramePoseAngles(angleMap: MutableMap<Int, Double>, side: String): Int
    abstract fun  getTotalReps(): Int
    abstract fun resetTotalReps()
}