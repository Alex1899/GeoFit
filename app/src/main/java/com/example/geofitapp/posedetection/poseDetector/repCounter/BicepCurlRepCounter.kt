package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor.Companion.exerciseFinished
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.BicepCurlProcessor
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils
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

    override var overallTotalReps: Int? = null
    private var exerciseProcessor = BicepCurlProcessor

    private const val DEFAULT_ENTER_THRESHOLD = 90
    private const val DEFAULT_EXIT_THRESHOLD = 130


    override fun addNewFramePoseAngles(
        angleMap: MutableMap<Int, Double>,
        side: String
    ): ExerciseProcessor {
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


        val maxInAngleList = max(anglesList)
        if (maxElbowAngle == null) {
            maxElbowAngle = maxInAngleList
            BicepCurlProcessor.lastRepResult = totalReps
            BicepCurlProcessor.pace = finishTime
            return BicepCurlProcessor

        } else if (freshAngle >= maxElbowAngle!!) {
            if(!poseEntered){
                val index = anglesList.indexOf(freshAngle)
                anglesList = anglesList.slice(index until anglesList.size).toMutableList()
            }
            maxElbowAngle = freshAngle
            Log.i("RepCount", "maxAngle updated=${freshAngle}")

            return BicepCurlProcessor
        } else {
            // if rep count reached return
            if (overallTotalReps != null && totalReps == overallTotalReps!! && poseEntered) {
                poseEntered()
                BicepCurlProcessor.finished =  true // finished true
                BicepCurlProcessor.anglesOfInterest.add(BicepCurlProcessor.elbowAnglePairList)
                exerciseFinished = true
                return BicepCurlProcessor
            }

            if (startTime == null) {
                startTime = System.currentTimeMillis()
                Log.i("Pace", "start time initialized")
                Log.i("Pace", "startTime = $startTime")
            }

            if (maxElbowAngle!! - freshAngle <= 30) {
                return BicepCurlProcessor
            }

            if(poseEntered){
                poseEntered()
                return BicepCurlProcessor
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
                analysisAngleList = anglesList.toMutableList()
                anglesList.clear()
                anglesList.add(freshAngle)
                Log.i("RepCount", "anglesList sliced=$anglesList")
                maxElbowAngle = null
                poseEntered = true
                totalReps++
                BicepCurlProcessor.lastRepResult = totalReps
            }
        }
        Log.i("RepCount", "\n\nminAngle=$minElbowAngle\n\n")

        return BicepCurlProcessor

    }

    private fun poseEntered(){
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
        analysisAngleList.addAll(anglesList.slice(1..index + 1))

        anglesList = anglesList.subList(index + 1, anglesList.size)

        BicepCurlProcessor.pace = finishTime
        if(maxElbowAngle != null && minElbowAngle != null){ // both should never be null here
            val filteredAngleList = Utils.medfilt(analysisAngleList.toMutableList(), 5)
            val filteredAngleList2 = Utils.medfilt(filteredAngleList, 5)

            BicepCurlProcessor.elbowAnglePairList[Pair(maxElbowAngle!!, minElbowAngle!!)] = filteredAngleList2.distinct().toMutableList()
            BicepCurlProcessor.repFinished = true
        }

        maxElbowAngle = null
        minElbowAngle = null
        Log.i(
            "RepCount", "===================\n" +
                    "Rep $totalReps \n==================="
        )
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
        anglesList.clear()
        maxElbowAngle = null
        minElbowAngle = null
        finishTime = 0f
        startTime = null
        BicepCurlProcessor.resetDetails()
    }


}