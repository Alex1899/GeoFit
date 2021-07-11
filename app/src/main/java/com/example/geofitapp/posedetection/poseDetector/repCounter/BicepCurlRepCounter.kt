package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Collections.max
import java.util.Collections.min

class BicepCurlRepCounter() : ExerciseRepCounter() {
    private var totalReps = 0
    private var startingAngle: Double? = null
    private var leftStartingAngle: Double? = null
    private var rightStartingAngle: Double? = null
    private var poseEntered = false
    private var maxElbowAngle: Double? = null
    private var minElbowAngle: Double? = null
    private var maxAngleReinitialized = false
    private var anglesList = mutableListOf<Double>()


    companion object {
        private const val DEFAULT_ENTER_THRESHOLD = 90
        private const val DEFAULT_EXIT_THRESHOLD = 138
    }

    override fun addNewFramePoseAngles(angleMap: MutableMap<Int, Double>, side: String): Int {
        val id = if (side == "right") {
            PoseLandmark.RIGHT_ELBOW
        } else {
            PoseLandmark.LEFT_ELBOW
        }
        anglesList.add(angleMap[id]!!)
        Log.i("RepCount", "fresh anglesList=$anglesList")


        // angleList [160, 163, ... min peak, ... max peak ]
        // [160, 163, 160, 164, 165, 163, 160, 150, 140, ...
        val maxInArray = max(anglesList)
        if (maxElbowAngle == null) {
            maxElbowAngle = maxInArray
            return totalReps
        } else if (maxInArray > maxElbowAngle!!) {
            maxElbowAngle = maxInArray
            Log.i("RepCount", "maxAngle updated=${maxInArray}")
            return totalReps
        }else {
            val lastAngle = anglesList.last()
            if(maxInArray == maxElbowAngle!! && maxInArray - lastAngle < 5){
                return totalReps
            }
            if (poseEntered) {
                Log.i("RepCount", "last maxAngle=$maxElbowAngle")
                poseEntered = false
                totalReps++
                val index = anglesList.indexOf(maxElbowAngle)
                anglesList = anglesList.subList(index+1, anglesList.size)

                maxElbowAngle = null
                minElbowAngle = null
                Log.i("RepCount", "===================\n" +
                        "Rep $totalReps \n===================")
                return totalReps
            }
        }
        Log.i("RepCount", "maxAngle=$maxElbowAngle")


        // min angle
        val minInArray = min(anglesList)
        if (minElbowAngle == null) {
            minElbowAngle = minInArray
            return totalReps
        } else if (minInArray < minElbowAngle!!) {
            minElbowAngle = minInArray
            Log.i("RepCount", "current min updated to=$minInArray")
            return totalReps
        } else {
            if (anglesList.last() - minElbowAngle!! >= 30) {
                // weight going down
                val lastIndex = anglesList.lastIndex
                anglesList = anglesList.slice(lastIndex-1..lastIndex) as MutableList<Double>
                Log.i("RepCount", "anglesList sliced=$anglesList")
                maxElbowAngle = null
                poseEntered = true
            }
        }
        Log.i("RepCount", "minAngle=$minElbowAngle")

        return totalReps


    }

//    override fun addNewFramePoseAngles(poseAnglesMap: MutableMap<Int, Double>, side: String): Int {
//        if (side === "front") {
//            return addFrontPose(poseAnglesMap)
//        }
//
//        val id = if (side === "right") {
//            PoseLandmark.RIGHT_ELBOW
//        } else {
//            PoseLandmark.LEFT_ELBOW
//        }
//
//        val elbowAngle = poseAnglesMap[id]!!
//
//        if (!poseEntered) {
//            poseEntered = elbowAngle < DEFAULT_ENTER_THRESHOLD
//            return totalReps
//        }
//
//        if (elbowAngle >= DEFAULT_EXIT_THRESHOLD) {
//            totalReps++
//            poseEntered = false
//        }
//        return totalReps
//    }

    private fun addFrontPose(poseAnglesMap: MutableMap<Int, Double>): Int {
        val rightElbowAngle = poseAnglesMap[PoseLandmark.RIGHT_ELBOW]!!
        val leftElbowAngle = poseAnglesMap[PoseLandmark.LEFT_ELBOW]!!

        return if (leftStartingAngle == null && rightStartingAngle == null) {
            leftStartingAngle = leftElbowAngle
            rightStartingAngle = rightElbowAngle
            0
        } else {
            //pose entered
            if (leftStartingAngle!! - leftElbowAngle >= 30 &&
                rightStartingAngle!! - rightElbowAngle >= 30 && !poseEntered
            ) {
                totalReps++
                poseEntered = true
                return totalReps
            }

            //pose exited
            if (leftStartingAngle!! - leftElbowAngle < 30 &&
                rightStartingAngle!! - rightElbowAngle < 30
            ) {
                poseEntered = false
            }
            return totalReps

        }

    }

    override fun getTotalReps(): Int {
        return totalReps
    }

    override fun resetTotalReps() {
        totalReps = 0
    }


}