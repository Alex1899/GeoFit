package com.example.geofitapp.posedetection.poseDetector.repCounter

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*

object FrontExerciseRepCounter : ExerciseRepCounter() {
    private var totalReps = 0
    private var poseEntered = false
    private var leftMaxAngle: Double? = null
    private var rightMaxAngle: Double? = null
    private var leftMinAngle: Double? = null
    private var rightMinAngle: Double? = null
    private var analysisAngleListMap = mutableMapOf<Int, MutableList<Double>>()
    private var startTime: Long? = null
    private var exerciseStartTime: Long? = null
    private var finishTime: Float = 0f
    private var paceAvgList = mutableListOf<Float>()

    private var aoiListMap = mutableMapOf<Int, MutableList<Double>>()
    override var overallTotalReps: Int? = null

    private var increaseListLeft: Double = 0.0
    private var increaseListRight: Double = 0.0

    private var leftLastRepStart: Double? = null
    private var rightLastRepStart: Double? = null


    override fun init(side: String) {
        aoiListMap = mutableMapOf(
            PoseLandmark.RIGHT_ELBOW to mutableListOf(),
            PoseLandmark.RIGHT_SHOULDER to mutableListOf(),

            PoseLandmark.LEFT_ELBOW to mutableListOf(),
            PoseLandmark.LEFT_SHOULDER to mutableListOf(),
        )
    }

    override fun addNewFramePoseAngles(
        angleMap: MutableMap<Int, Double>,
        mainAOIidx: List<Int>
    ): ExerciseProcessor {
        val leftFreshElbowAngle = angleMap[PoseLandmark.LEFT_ELBOW]!!
        val leftFreshShoulderAngle = angleMap[PoseLandmark.LEFT_SHOULDER]!!

        val rightFreshElbowAngle = angleMap[PoseLandmark.RIGHT_ELBOW]!!
        val rightFreshShoulderAngle = angleMap[PoseLandmark.RIGHT_SHOULDER]!!

        aoiListMap[PoseLandmark.LEFT_ELBOW]!!.add(leftFreshElbowAngle)
        aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.add(leftFreshShoulderAngle)

        aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.add(rightFreshElbowAngle)
        aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.add(rightFreshShoulderAngle)

        val leftMainAOIindex = mainAOIidx[0]
        val rightMainAOIindex = mainAOIidx[1]

        val leftMainAngle = angleMap[leftMainAOIindex]!!
        val rightMainAngle = angleMap[rightMainAOIindex]!!


        if (leftMinAngle == null && rightMinAngle == null) {
            leftMinAngle = Collections.min(aoiListMap[leftMainAOIindex]!!)
            rightMinAngle = Collections.min(aoiListMap[rightMainAOIindex]!!)

            ExerciseProcessor.lastRepResult = totalReps
            ExerciseProcessor.pace = finishTime
            return ExerciseProcessor

        } else if (leftMainAngle <= leftMinAngle!! && rightMainAngle <= rightMinAngle!!) {
            if (!poseEntered) {
                val index1 = aoiListMap[leftMainAOIindex]!!.indexOf(leftMainAngle)
                val index2 = aoiListMap[rightMainAOIindex]!!.indexOf(rightMainAngle)

                Log.i(
                    "CheckIndex",
                    "left main angle idx = $index1 size = ${aoiListMap[leftMainAOIindex]!!.size - 1}"
                )
                Log.i(
                    "CheckIndex",
                    "right main angle idx = $index2 size = ${aoiListMap[rightMainAOIindex]!!.size - 1}"
                )


                aoiListMap[PoseLandmark.LEFT_ELBOW] =
                    aoiListMap[PoseLandmark.LEFT_ELBOW]!!.slice(index1 until aoiListMap[PoseLandmark.LEFT_ELBOW]!!.size)
                        .toMutableList()
                aoiListMap[PoseLandmark.RIGHT_ELBOW] =
                    aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.slice(index2 until aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.size)
                        .toMutableList()

                aoiListMap[PoseLandmark.LEFT_SHOULDER] =
                    aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.slice(index1 until aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.size)
                        .toMutableList()
                aoiListMap[PoseLandmark.RIGHT_SHOULDER] =
                    aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.slice(index2 until aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.size)
                        .toMutableList()
            }
            leftMinAngle = leftMainAngle
            rightMinAngle = rightMainAngle

            return ExerciseProcessor
        } else {

            // if rep count reached return
            if (overallTotalReps != null && totalReps == overallTotalReps!! && poseEntered) {
                // for last rep, if change is less than 5 continue
                if (kotlin.math.abs(leftMainAngle - leftLastRepStart!!) < 20 && kotlin.math.abs(
                        rightMainAngle - rightLastRepStart!!
                    ) < 20
                ) {
                    poseEntered(leftMainAOIindex, rightMainAOIindex)
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

            if (leftMainAngle - leftMinAngle!! <= 30 && rightMainAngle - rightMinAngle!! <= 30) {
//              return filterIncrease(leftMainAngle, rightMainAngle, leftMainAOIindex, rightMainAOIindex)
                return ExerciseProcessor
            }


            if (poseEntered) {
                poseEntered(leftMainAOIindex, rightMainAOIindex)
                return ExerciseProcessor
            }
        }



        // min angle
        if (leftMaxAngle == null && rightMaxAngle == null) {
            leftMaxAngle = leftMainAngle
            rightMaxAngle = rightMainAngle

        } else if (leftMaxAngle!! < leftMainAngle && rightMaxAngle!! < rightMainAngle) {
            leftMaxAngle = leftMainAngle
            rightMaxAngle = rightMainAngle
        } else {
            if (leftMaxAngle!! - leftMainAngle >= 30 && rightMaxAngle!! - rightMainAngle >= 30) {
                // weight going down
                analysisAngleListMap[PoseLandmark.LEFT_ELBOW] =
                    aoiListMap[PoseLandmark.LEFT_ELBOW]!!.toMutableList()
                analysisAngleListMap[PoseLandmark.RIGHT_ELBOW] =
                    aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.toMutableList()

                analysisAngleListMap[PoseLandmark.LEFT_SHOULDER] =
                    aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.toMutableList()
                analysisAngleListMap[PoseLandmark.RIGHT_SHOULDER] =
                    aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.toMutableList()

                aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.clear()
                aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.clear()

                aoiListMap[PoseLandmark.LEFT_ELBOW]!!.clear()
                aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.clear()

                aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.add(leftFreshShoulderAngle)
                aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.add(rightFreshShoulderAngle)

                aoiListMap[PoseLandmark.LEFT_ELBOW]!!.add(leftFreshElbowAngle)
                aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.add(rightFreshElbowAngle)

                // save start rep angles
                leftLastRepStart = leftMinAngle
                rightLastRepStart = rightMinAngle

                leftMinAngle = null
                rightMinAngle = null
                poseEntered = true
                totalReps++
                ExerciseProcessor.lastRepResult = totalReps
            }
        }

        return ExerciseProcessor

    }

    private fun getTimeDiff(time: Long): Float {
        val now = System.currentTimeMillis()
        val diff = (now - time).toFloat()
        return ((diff / 1000) % 60)
    }



    private fun poseEntered(leftMainidx: Int, rightMainidx: Int) {
        poseEntered = false

        val endTime = getTimeDiff(startTime!!)
        paceAvgList.add(endTime)
        finishTime = paceAvgList.average().toFloat()
        startTime = null

        val index1 = aoiListMap[leftMainidx]!!.indexOf(leftMinAngle)
        val index2 = aoiListMap[rightMainidx]!!.indexOf(rightMinAngle)

        analysisAngleListMap[PoseLandmark.LEFT_ELBOW]!!.addAll(
            aoiListMap[PoseLandmark.LEFT_ELBOW]!!.slice(1..index1 + 1)
        )
        analysisAngleListMap[PoseLandmark.RIGHT_ELBOW]!!.addAll(
            aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.slice(1..index2 + 1)
        )
        analysisAngleListMap[PoseLandmark.LEFT_SHOULDER]!!.addAll(
            aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.slice(1..index1 + 1)
        )
        analysisAngleListMap[PoseLandmark.RIGHT_SHOULDER]!!.addAll(
            aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.slice(1..index2 + 1)
        )


        aoiListMap[PoseLandmark.LEFT_ELBOW] =
            aoiListMap[PoseLandmark.LEFT_ELBOW]!!.slice(index1 + 1 until aoiListMap[PoseLandmark.LEFT_ELBOW]!!.size)
                .toMutableList()
        aoiListMap[PoseLandmark.RIGHT_ELBOW] =
            aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.slice(index2 + 1 until aoiListMap[PoseLandmark.RIGHT_ELBOW]!!.size)
                .toMutableList()

        aoiListMap[PoseLandmark.LEFT_SHOULDER] =
            aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.slice(index1 + 1 until aoiListMap[PoseLandmark.LEFT_SHOULDER]!!.size)
                .toMutableList()
        aoiListMap[PoseLandmark.RIGHT_SHOULDER] =
            aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.slice(index2 + 1 until aoiListMap[PoseLandmark.RIGHT_SHOULDER]!!.size)
                .toMutableList()


        ExerciseProcessor.pace = finishTime
        if (leftMaxAngle != null && rightMaxAngle !== null && leftMinAngle != null && rightMinAngle != null) { // both should never be null here
            val filteredLeftElbowAngleList =
                Utils.medfilt(analysisAngleListMap[PoseLandmark.LEFT_ELBOW]!!.toMutableList(), 5)
            val filteredLeftElbowAngleList2 = Utils.medfilt(filteredLeftElbowAngleList, 5)

            val filteredRightElbowAngleList =
                Utils.medfilt(analysisAngleListMap[PoseLandmark.RIGHT_ELBOW]!!.toMutableList(), 5)
            val filteredRightElbowAngleList2 = Utils.medfilt(filteredRightElbowAngleList, 5)

            val filteredLeftShoulderAngles =
                Utils.medfilt(analysisAngleListMap[PoseLandmark.LEFT_SHOULDER]!!.toMutableList(), 5)
            val filteredLeftShoulderAngles2 = Utils.medfilt(filteredLeftShoulderAngles, 5)

            val filteredRightShoulderAngles =
                Utils.medfilt(
                    analysisAngleListMap[PoseLandmark.RIGHT_SHOULDER]!!.toMutableList(),
                    5
                )
            val filteredRightShoulderAngles2 = Utils.medfilt(filteredRightShoulderAngles, 5)

            ExerciseProcessor.anglesOfInterest[PoseLandmark.LEFT_ELBOW] = Pair(
                if (leftMainidx == PoseLandmark.LEFT_ELBOW) Pair(
                    leftMaxAngle!!,
                    leftMinAngle!!
                ) else
                    Pair(
                        Collections.max(filteredLeftElbowAngleList2),
                        Collections.min(filteredLeftElbowAngleList2)
                    ),
                filteredLeftElbowAngleList2.distinct().toMutableList()
            )

            ExerciseProcessor.anglesOfInterest[PoseLandmark.RIGHT_ELBOW] = Pair(
                if (rightMainidx == PoseLandmark.RIGHT_ELBOW) Pair(
                    rightMaxAngle!!,
                    rightMinAngle!!
                ) else
                    Pair(
                        Collections.max(filteredRightElbowAngleList2),
                        Collections.min(filteredRightElbowAngleList2)
                    ),
                filteredRightElbowAngleList2.distinct().toMutableList()
            )
            ExerciseProcessor.anglesOfInterest[PoseLandmark.LEFT_SHOULDER] = Pair(
                if (leftMainidx == PoseLandmark.LEFT_SHOULDER) Pair(
                    leftMaxAngle!!,
                    leftMinAngle!!
                ) else
                    Pair(
                        Collections.max(filteredLeftShoulderAngles2),
                        Collections.min(filteredLeftShoulderAngles2)
                    ),
                filteredLeftShoulderAngles2.distinct().toMutableList()

            )

            ExerciseProcessor.anglesOfInterest[PoseLandmark.RIGHT_SHOULDER] = Pair(
                if (rightMainidx == PoseLandmark.RIGHT_SHOULDER) Pair(
                    rightMaxAngle!!,
                    rightMinAngle!!
                ) else
                    Pair(
                        Collections.max(filteredRightShoulderAngles2),
                        Collections.min(filteredRightShoulderAngles2)
                    ),
                filteredRightShoulderAngles2.distinct().toMutableList()

            )

            ExerciseProcessor.allAnglesOfInterest["elbow"]!!.second.first.addAll(
                filteredLeftElbowAngleList2
            )
            ExerciseProcessor.allAnglesOfInterest["elbow"]!!.second.second!!.addAll(
                filteredRightElbowAngleList2
            )


            ExerciseProcessor.allAnglesOfInterest["shoulder"]!!.second.first.addAll(
                filteredLeftShoulderAngles2
            )
            ExerciseProcessor.allAnglesOfInterest["shoulder"]!!.second.second!!.addAll(
                filteredRightShoulderAngles2
            )

            ExerciseProcessor.repFinished = true

        }

        leftMaxAngle = null
        rightMaxAngle = null
        leftMinAngle = null
        rightMinAngle = null

    }


    override fun getTotalReps(): Int {
        return totalReps
    }

    override fun resetTotalReps() {
        totalReps = 0
        paceAvgList.clear()
        aoiListMap.clear()
        analysisAngleListMap.clear()
        leftMaxAngle = null
        rightMaxAngle = null
        leftMinAngle = null
        rightMinAngle = null
        finishTime = 0f
        startTime = null
        exerciseStartTime = null
    }


}
