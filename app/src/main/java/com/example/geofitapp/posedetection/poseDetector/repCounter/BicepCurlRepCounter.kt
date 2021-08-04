package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor.Companion.exerciseFinished
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.BicepCurlProcessor
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Collections.max
import java.util.Collections.min

object BicepCurlRepCounter : ExerciseRepCounter() {
    private var totalReps = 0
    private var startingAngle: Double? = null
    private var leftStartingAngle: Double? = null
    private var rightStartingAngle: Double? = null
    private var poseEntered = false
    private var maxElbowAngle: Double? = null
    private var minElbowAngle: Double? = null
    private var analysisAngleListMap = mutableMapOf<Int, MutableList<Double>>()
    private var startTime: Long? = null
    private var exerciseStartTime: Long? = null
    private var finishTime: Float = 0f
    private var paceAvgList = mutableListOf<Float>()

    private var elbowId: Int = -1
    private var shoulderId: Int = -1
    private var hipId: Int = -1

    private var elbowAngles = mutableListOf<Double>()
    private var shoulderAngles = mutableListOf<Double>()
    private var hipAngles = mutableListOf<Double>()

    override var overallTotalReps: Int? = null


    private const val DEFAULT_ENTER_THRESHOLD = 90
    private const val DEFAULT_EXIT_THRESHOLD = 130


    override fun addNewFramePoseAngles(
        angleMap: MutableMap<Int, Double>,
        side: String
    ): ExerciseProcessor {
        if (side == "right") {
            elbowId = PoseLandmark.RIGHT_ELBOW
            shoulderId = PoseLandmark.RIGHT_SHOULDER
            hipId = PoseLandmark.RIGHT_HIP
        } else {
            elbowId = PoseLandmark.LEFT_ELBOW
            shoulderId = PoseLandmark.LEFT_SHOULDER
            hipId = PoseLandmark.LEFT_HIP
        }

        val freshAngle = angleMap[elbowId]!!
        val freshShoulderAngle = angleMap[shoulderId]!!
        val freshHipAngle = angleMap[hipId]!!
        elbowAngles.add(freshAngle)
        shoulderAngles.add(freshShoulderAngle)
        hipAngles.add(freshHipAngle)

        Log.i("RepCountAfter", "ElbowAngles=${elbowAngles.size}")
        Log.i("RepCountAfter", "ShoulderAngles=${shoulderAngles.size}")
        Log.i("RepCountAfter", "HipAngles=${hipAngles.size}")


        Log.i("RepCount", "==================================================================")
        Log.i("RepCount", "Initial maxAngle=$maxElbowAngle    minAngle=$minElbowAngle")
        Log.i("RepCount", "==================================================================")


        val maxInAngleList = max(elbowAngles)
        if (maxElbowAngle == null) {
            maxElbowAngle = maxInAngleList
            BicepCurlProcessor.lastRepResult = totalReps
            BicepCurlProcessor.pace = finishTime
            return BicepCurlProcessor

        } else if (freshAngle >= maxElbowAngle!!) {
            if (!poseEntered) {
                val index = elbowAngles.indexOf(freshAngle)
                elbowAngles = elbowAngles.slice(index until elbowAngles.size).toMutableList()
                shoulderAngles =
                    shoulderAngles.slice(index until shoulderAngles.size).toMutableList()
                hipAngles = hipAngles.slice(index until hipAngles.size).toMutableList()
            }
            maxElbowAngle = freshAngle
            Log.i("RepCount", "maxAngle updated=${freshAngle}")

            return BicepCurlProcessor
        } else {
            // if rep count reached return
            if (overallTotalReps != null && totalReps == overallTotalReps!! && poseEntered) {
                poseEntered()
                BicepCurlProcessor.finished = true // finished true
                BicepCurlProcessor.exerciseFinishTime = getTimeDiff(exerciseStartTime!!)
                exerciseStartTime = null
                exerciseFinished = true
                return BicepCurlProcessor
            }

            if (startTime == null) {
                startTime = System.currentTimeMillis()
            }
            if (exerciseStartTime == null) {
                exerciseStartTime = System.currentTimeMillis()
            }

            if (maxElbowAngle!! - freshAngle <= 30) {
                return BicepCurlProcessor
            }

            if (poseEntered) {
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
                analysisAngleListMap[elbowId] = elbowAngles.toMutableList()
                analysisAngleListMap[shoulderId] = shoulderAngles.toMutableList()
                analysisAngleListMap[hipId] = hipAngles.toMutableList()

                elbowAngles.clear()
                shoulderAngles.clear()
                hipAngles.clear()

                elbowAngles.add(freshAngle)
                shoulderAngles.add(freshShoulderAngle)
                hipAngles.add(freshHipAngle)

                Log.i("RepCount", "anglesList sliced=${elbowAngles}")
                maxElbowAngle = null
                poseEntered = true
                totalReps++
                BicepCurlProcessor.lastRepResult = totalReps
            }
        }
        Log.i("RepCount", "\n\nminAngle=$minElbowAngle\n\n")

        return BicepCurlProcessor

    }

    private fun getTimeDiff(time: Long): Float {
        val now = System.currentTimeMillis()
        val diff = (now - time).toFloat()
        return ((diff / 1000) % 60)
    }

    private fun poseEntered() {
        Log.i("RepCount", "last maxAngle=$maxElbowAngle")
        poseEntered = false

        val endTime = getTimeDiff(startTime!!)
        paceAvgList.add(endTime)
        finishTime = paceAvgList.average().toFloat()
        startTime = null

        Log.i("Pace", "finishTime = ${String.format("%.1f", finishTime)}")

        val index = elbowAngles.indexOf(maxElbowAngle)
        analysisAngleListMap[elbowId]!!.addAll(elbowAngles.slice(1..index + 1))
        analysisAngleListMap[shoulderId]!!.addAll(shoulderAngles.slice(1..index +1))
        analysisAngleListMap[hipId]!!.addAll(hipAngles.slice(1..index + 1))

        elbowAngles = elbowAngles.slice(index + 1 until elbowAngles.size).toMutableList()
        shoulderAngles = shoulderAngles.slice(index + 1 until shoulderAngles.size).toMutableList()
        hipAngles = hipAngles.slice(index + 1 until hipAngles.size).toMutableList()


        Log.i("RepCountAfter", "ElbowAngles after=${elbowAngles.size}")
        Log.i("RepCountAfter", "ShoulderAngles after=${shoulderAngles.size}")
        Log.i("RepCountAfter", "HipAngles after=${hipAngles.size}")



        BicepCurlProcessor.pace = finishTime
        if (maxElbowAngle != null && minElbowAngle != null) { // both should never be null here
            val filteredElbowAngleList =
                Utils.medfilt(analysisAngleListMap[elbowId]!!.toMutableList(), 5)
            val filteredElbowAngleList2 = Utils.medfilt(filteredElbowAngleList, 5)

            val filteredShoulderAngles =
                Utils.medfilt(analysisAngleListMap[shoulderId]!!.toMutableList(), 5)
            val filteredShoulderAngles2 = Utils.medfilt(filteredShoulderAngles, 5)

            val filteredHipAngles = Utils.medfilt(analysisAngleListMap[hipId]!!.toMutableList(), 5)
            val filteredHipAngles2 = Utils.medfilt(filteredHipAngles, 5)

            BicepCurlProcessor.anglesOfInterest[elbowId] = Pair(
                Pair(maxElbowAngle!!, minElbowAngle!!),
                filteredElbowAngleList2.distinct().toMutableList()
            )
            BicepCurlProcessor.anglesOfInterest[shoulderId] = Pair(
                Pair(max(filteredShoulderAngles2), min(filteredShoulderAngles2)),
                filteredShoulderAngles2.distinct().toMutableList()

            )
            BicepCurlProcessor.anglesOfInterest[hipId] = Pair(
                Pair(max(filteredHipAngles2), 0.0),
                filteredHipAngles2.distinct().toMutableList()
            )

            BicepCurlProcessor.allAnglesOfInterest["elbow"]!!.second.addAll(filteredElbowAngleList2)
            BicepCurlProcessor.allAnglesOfInterest["shoulder"]!!.second.addAll(filteredShoulderAngles2)
            BicepCurlProcessor.allAnglesOfInterest["hip"]!!.second.addAll(filteredHipAngles2)

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
        elbowAngles.clear()
        shoulderAngles.clear()
        hipAngles.clear()
        maxElbowAngle = null
        minElbowAngle = null
        finishTime = 0f
        startTime = null
        exerciseStartTime = null
        BicepCurlProcessor.resetDetails()
    }


}