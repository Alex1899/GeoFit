package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.google.common.base.Stopwatch
import com.google.mlkit.vision.pose.PoseLandmark
import java.time.Instant
import java.util.Collections.max
import java.util.Collections.min
import java.util.concurrent.TimeUnit

object BicepCurlRepCounter : ExerciseRepCounter() {
    private var totalReps = 0
    private var startingAngle: Double? = null
    private var leftStartingAngle: Double? = null
    private var rightStartingAngle: Double? = null
    private var poseEntered = false
    private var maxElbowAngle: Double? = null
    private var minElbowAngle: Double? = null
    private var anglesList = mutableListOf<Double>()
    private var analysisAngleList = mutableListOf<Double>()
    private var startTime: Long? = null
    private var finishTime: Float = 0f
    private var paceAvgList = mutableListOf<Float>()

    private const val DEFAULT_ENTER_THRESHOLD = 90
    private const val DEFAULT_EXIT_THRESHOLD = 130


    override fun addNewFramePoseAngles(
        angleMap: MutableMap<Int, Double>,
        side: String
    ): Triple<Int, Float, MutableList<Double>?> {
        val id = if (side == "right") {
            PoseLandmark.RIGHT_ELBOW
        } else {
            PoseLandmark.LEFT_ELBOW
        }
        val freshAngle = angleMap[id]!!
        anglesList.add(freshAngle)

        Log.i("RepCount", "fresh angles=$anglesList")
        Log.i("RepCount", "==================================================================")
        Log.i("RepCount", "Initial maxAngle=$maxElbowAngle    minAngle=$minElbowAngle")
        Log.i("RepCount", "==================================================================")



        if (maxElbowAngle == null) {
            maxElbowAngle = freshAngle
            return Triple(totalReps, finishTime, null)

        } else if (freshAngle >= maxElbowAngle!!) {
            maxElbowAngle = freshAngle
            Log.i("RepCount", "maxAngle updated=${freshAngle}")
            return Triple(totalReps, finishTime, null)
        } else {
            // TODO
            // if rep count reached return

            if (startTime == null) {
                startTime = System.currentTimeMillis()
                Log.i("Pace", "start time initialized")
                Log.i("Pace", "startTime = $startTime")
            }

            if ( maxElbowAngle!! - freshAngle <= 30) {
                return Triple(totalReps, finishTime, null)
            }

            if (poseEntered) {
                Log.i("RepCount", "last maxAngle=$maxElbowAngle")
                poseEntered = false
                val now = System.currentTimeMillis()
                val diff = (now - startTime!!).toFloat()


                val endTime = ((diff / 1000) % 60)
                paceAvgList.add(endTime)
                finishTime = paceAvgList.average().toFloat()
                startTime = null

                Log.i("Pace", "finishTime = ${String.format("%.1f", finishTime)}")

                val index = anglesList.indexOf(maxElbowAngle)
                analysisAngleList = anglesList.subList(0, index + 1)

                anglesList = anglesList.subList(index + 1, anglesList.size)
                maxElbowAngle = null
                minElbowAngle = null
                Log.i(
                    "RepCount", "===================\n" +
                            "Rep $totalReps \n==================="
                )
                return Triple(totalReps, finishTime, analysisAngleList)
            }
        }
        Log.i("RepCount", "maxAngle=$maxElbowAngle")


        // min angle
        if (minElbowAngle == null) {
            minElbowAngle = freshAngle
            Log.i("RepCount", "minAngle set = $minElbowAngle")
        } else if (minElbowAngle!! > freshAngle) {
            minElbowAngle = freshAngle
            Log.i("RepCount", "current min updated to=$freshAngle")
        } else {
            if (freshAngle - minElbowAngle!! >= 30) {
                // weight going down
                val lastIndex = anglesList.lastIndex
                anglesList = anglesList.slice(lastIndex - 1..lastIndex) as MutableList<Double>
                Log.i("RepCount", "anglesList sliced=$anglesList")
                maxElbowAngle = null
                poseEntered = true
                totalReps++

            }
        }
        Log.i("RepCount", "\n\nminAngle=$minElbowAngle\n\n")

        return Triple(totalReps, finishTime, null)


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
        paceAvgList.clear()
        finishTime = 0f
        startTime = null
    }


}