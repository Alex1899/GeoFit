package com.example.geofitapp.posedetection.poseDetector.repCounter

abstract class ExerciseRepCounter {
    abstract fun addNewFramePoseAngles(angleMap: MutableMap<Int, Double>, side: String): Triple<Int, Float, MutableList<Double>?>
    abstract fun getTotalReps(): Int
    abstract fun resetTotalReps()
}