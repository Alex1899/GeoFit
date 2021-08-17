package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*

object SideMinMaxRepCounter : ExerciseRepCounter() {
    private var totalReps = 0
    private var poseEntered = false
    private var maxAngle: Double? = null
    private var minAngle: Double? = null
    private var analysisAngleListMap = mutableMapOf<Int, MutableList<Double>>()
    private var startTime: Long? = null
    private var exerciseStartTime: Long? = null
    private var finishTime: Float = 0f
    private var paceAvgList = mutableListOf<Float>()

    private var elbowId: Int = -1
    private var shoulderId: Int = -1
    private var hipId: Int = -1


    private var aoiListMap = mutableMapOf<Int, MutableList<Double>>()
    override var overallTotalReps: Int? = null

    private var lastRepStart: Double? = null


    override fun init(side: String) {
        if (side == "right") {
            elbowId = PoseLandmark.RIGHT_ELBOW
            shoulderId = PoseLandmark.RIGHT_SHOULDER
            hipId = PoseLandmark.RIGHT_HIP
        } else {
            elbowId = PoseLandmark.LEFT_ELBOW
            shoulderId = PoseLandmark.LEFT_SHOULDER
            hipId = PoseLandmark.LEFT_HIP
        }

        aoiListMap = mutableMapOf(
            elbowId to mutableListOf(),
            shoulderId to mutableListOf(),
            hipId to mutableListOf()
        )
    }

    override fun addNewFramePoseAngles(
        angleMap: MutableMap<Int, Double>,
        mainAOIidx: List<Int>
    ): ExerciseProcessor {
        val freshElbowAngle = angleMap[elbowId]!!
        val freshShoulderAngle = angleMap[shoulderId]!!
        val freshHipAngle = angleMap[hipId]!!
        aoiListMap[elbowId]!!.add(freshElbowAngle)
        aoiListMap[shoulderId]!!.add(freshShoulderAngle)
        aoiListMap[hipId]!!.add(freshHipAngle)
        val mainAOIindex = mainAOIidx[0]

        val mainAoiAngle = angleMap[mainAOIindex]!!

        Log.i("RaiseCheck", "freshAngles = ${aoiListMap[mainAOIindex]!!}")
        Log.i("RaiseCheck", "minAngle = $minAngle maxAngle = $maxAngle")

        if (minAngle == null) {
            minAngle = Collections.min(aoiListMap[mainAOIindex]!!)
            ExerciseProcessor.lastRepResult = totalReps
            ExerciseProcessor.pace = finishTime
            return ExerciseProcessor

        } else if (mainAoiAngle <= minAngle!!) {
            if (!poseEntered) {
                val index = aoiListMap[mainAOIindex]!!.lastIndex
                aoiListMap[elbowId] =
                    aoiListMap[elbowId]!!.slice(index until aoiListMap[elbowId]!!.size)
                        .toMutableList()
                aoiListMap[shoulderId] =
                    aoiListMap[shoulderId]!!.slice(index until aoiListMap[shoulderId]!!.size)
                        .toMutableList()
                aoiListMap[hipId] =
                    aoiListMap[hipId]!!.slice(index until aoiListMap[hipId]!!.size).toMutableList()
            }
            Log.i("RaiseCheck", "minAngle updated to new min angle = $mainAoiAngle")

            minAngle = mainAoiAngle

            return ExerciseProcessor
        } else {

            // if rep count reached return
            if (overallTotalReps != null && totalReps == overallTotalReps!! && poseEntered) {
                // for last rep, if change is less than 5 continue
                if (kotlin.math.abs(minAngle!! - lastRepStart!!) < 10) {
                    poseEntered(mainAOIindex)
                    ExerciseProcessor.finished = true // finished true
                    ExerciseProcessor.exerciseFinishTime = getTimeDiff(exerciseStartTime!!)
                    exerciseStartTime = null
                    PoseDetectorProcessor.exerciseFinished = true
                    resetTotalReps()
                }
                return ExerciseProcessor
            }

            if (startTime == null) {
                startTime = System.currentTimeMillis()
            }
            if (exerciseStartTime == null) {
                exerciseStartTime = System.currentTimeMillis()
            }

            if (mainAoiAngle - minAngle!! <= 30) {
                return ExerciseProcessor
            }

            if (poseEntered) {
                poseEntered(mainAOIindex)
                return ExerciseProcessor
            }

        }

        // min angle
        if (maxAngle == null) {
            maxAngle = mainAoiAngle
        } else if (maxAngle!! < mainAoiAngle) {
            Log.i("RaiseCheck", "maxAngle updated to new max angle $mainAoiAngle")

            maxAngle = mainAoiAngle
        } else {
            if (maxAngle!! - mainAoiAngle >= 30) {
                // weight going down
                Log.i("RaiseCheck", "===================")
                Log.i("RaiseCheck", "Going down; minAngle = $minAngle maxAngle = $maxAngle")
                Log.i("RaiseCheck", "===================")


                analysisAngleListMap[elbowId] = aoiListMap[elbowId]!!.toMutableList()
                analysisAngleListMap[shoulderId] = aoiListMap[shoulderId]!!.toMutableList()
                analysisAngleListMap[hipId] = aoiListMap[hipId]!!.toMutableList()

                aoiListMap[elbowId]!!.clear()
                aoiListMap[shoulderId]!!.clear()
                aoiListMap[hipId]!!.clear()

                aoiListMap[elbowId]!!.add(freshElbowAngle)
                aoiListMap[shoulderId]!!.add(freshShoulderAngle)
                aoiListMap[hipId]!!.add(freshHipAngle)

                Log.i("RaiseCheck", "anglesList cleared = ${aoiListMap[mainAOIindex]!!}")

                // save the starting main aoi angle for comparison

                lastRepStart = minAngle
                minAngle = null
                poseEntered = true
                totalReps++
                ExerciseProcessor.lastRepResult = totalReps
                Log.i("RaiseCheck", "===========================")
                Log.i("RaiseCheck", "Rep $totalReps")
                Log.i("RaiseCheck", "===========================")


            }
        }

        return ExerciseProcessor

    }

    private fun getTimeDiff(time: Long): Float {
        val now = System.currentTimeMillis()
        val diff = (now - time).toFloat()
        return ((diff / 1000) % 60)
    }

    private fun poseEntered(mainAOIindex: Int) {
        poseEntered = false

        val endTime = getTimeDiff(startTime!!)
        paceAvgList.add(endTime)
        finishTime = paceAvgList.average().toFloat()
        startTime = null

        val index = aoiListMap[mainAOIindex]!!.indexOf(minAngle)
        if (analysisAngleListMap.isNotEmpty()) {
            analysisAngleListMap[elbowId]!!.addAll(
                aoiListMap[elbowId]!!.slice(1..index + 1)
            )
            analysisAngleListMap[shoulderId]!!.addAll(
                aoiListMap[shoulderId]!!.slice(1..index + 1)
            )
            analysisAngleListMap[hipId]!!.addAll(
                aoiListMap[hipId]!!.slice(1..index + 1)
            )

            aoiListMap[elbowId] =
                aoiListMap[elbowId]!!.slice(index + 1 until aoiListMap[elbowId]!!.size)
                    .toMutableList()
            aoiListMap[shoulderId] =
                aoiListMap[shoulderId]!!.slice(index + 1 until aoiListMap[shoulderId]!!.size)
                    .toMutableList()
            aoiListMap[hipId] =
                aoiListMap[hipId]!!.slice(index + 1 until aoiListMap[hipId]!!.size).toMutableList()


            ExerciseProcessor.pace = finishTime
            if (maxAngle != null && minAngle != null) { // both should never be null here
                val filteredElbowAngleList =
                    Utils.medfilt(analysisAngleListMap[elbowId]!!.toMutableList(), 5)
                val filteredElbowAngleList2 = Utils.medfilt(filteredElbowAngleList, 5)

                val filteredShoulderAngles =
                    Utils.medfilt(analysisAngleListMap[shoulderId]!!.toMutableList(), 5)
                val filteredShoulderAngles2 = Utils.medfilt(filteredShoulderAngles, 5)

                val filteredHipAngles =
                    Utils.medfilt(analysisAngleListMap[hipId]!!.toMutableList(), 5)
                val filteredHipAngles2 = Utils.medfilt(filteredHipAngles, 5)

                ExerciseProcessor.anglesOfInterest[elbowId] = Pair(
                    if (mainAOIindex == elbowId) Pair(maxAngle!!, minAngle!!) else Pair(
                        Collections.max(filteredElbowAngleList2),
                        Collections.min(filteredElbowAngleList2)
                    ),
                    filteredElbowAngleList2.distinct().toMutableList()
                )
                ExerciseProcessor.anglesOfInterest[shoulderId] = Pair(
                    if (mainAOIindex == shoulderId) Pair(maxAngle!!, minAngle!!) else
                        Pair(
                            Collections.max(filteredShoulderAngles2),
                            Collections.min(filteredShoulderAngles2)
                        ),
                    filteredShoulderAngles2.distinct().toMutableList()

                )
                ExerciseProcessor.anglesOfInterest[hipId] = Pair(
                    if (mainAOIindex == hipId) Pair(maxAngle!!, minAngle!!) else
                        Pair(
                            Collections.max(filteredHipAngles2),
                            Collections.min(filteredHipAngles2)
                        ),
                    filteredHipAngles2.distinct().toMutableList()
                )

//                ExerciseProcessor.allAnglesOfInterest["elbow"]!!.second.first.addAll(
//                    filteredElbowAngleList2
//                )
//                ExerciseProcessor.allAnglesOfInterest["shoulder"]!!.second.first.addAll(
//                    filteredShoulderAngles2
//                )
//                ExerciseProcessor.allAnglesOfInterest["hip"]!!.second.first.addAll(
//                    filteredHipAngles2
//                )
                ExerciseProcessor.allAnglesOfInterest["elbow"]!!.second.first.addAll(
                    filteredElbowAngleList2
                )
                ExerciseProcessor.allAnglesOfInterest["shoulder"]!!.second.first.addAll(
                    filteredShoulderAngles2
                )
                ExerciseProcessor.allAnglesOfInterest["hip"]!!.second.first.addAll(
                    filteredHipAngles2
                )

                ExerciseProcessor.repFinished = true

            }

            maxAngle = null
            minAngle = null

        }
    }


    override fun getTotalReps(): Int {
        return totalReps
    }

    override fun resetTotalReps() {
        totalReps = 0
        paceAvgList.clear()
        aoiListMap.clear()
        analysisAngleListMap.clear()
        maxAngle = null
        minAngle = null
        finishTime = 0f
        startTime = null
        exerciseStartTime = null
    }
}