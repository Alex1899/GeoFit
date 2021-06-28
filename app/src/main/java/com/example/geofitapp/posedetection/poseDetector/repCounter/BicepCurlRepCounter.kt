package com.example.geofitapp.posedetection.poseDetector.repCounter

import com.google.mlkit.vision.pose.PoseLandmark

class BicepCurlRepCounter : ExerciseRepCounter() {
    private var totalReps = 0
    private var poseEntered = false

    companion object {
        private const val DEFAULT_ENTER_THRESHOLD = 90
        private const val DEFAULT_EXIT_THRESHOLD = 138
    }

    override fun addNewFramePoseAngles(poseAnglesMap: MutableMap<Int, Double>): Int{
        val rightElbowAngle = poseAnglesMap[PoseLandmark.RIGHT_ELBOW]!!
        val leftElbowAngle = poseAnglesMap[PoseLandmark.LEFT_ELBOW]!!

        if(!poseEntered){
            poseEntered = rightElbowAngle < DEFAULT_ENTER_THRESHOLD && leftElbowAngle < DEFAULT_ENTER_THRESHOLD
            return totalReps
        }

        if(rightElbowAngle >= DEFAULT_EXIT_THRESHOLD && leftElbowAngle >= DEFAULT_EXIT_THRESHOLD){
            totalReps++
            poseEntered = false
        }
        return totalReps
    }

    override fun getTotalReps(): Int {
        return totalReps
    }


}