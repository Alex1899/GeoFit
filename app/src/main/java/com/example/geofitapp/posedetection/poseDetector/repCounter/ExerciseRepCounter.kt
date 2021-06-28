package com.example.geofitapp.posedetection.poseDetector.repCounter

abstract class ExerciseRepCounter {
    abstract fun addNewFramePoseAngles(poseAnglesMap: MutableMap<Int, Double>): Int
    abstract fun  getTotalReps(): Int
}