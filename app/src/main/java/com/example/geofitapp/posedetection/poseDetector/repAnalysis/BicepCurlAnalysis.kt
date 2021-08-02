package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.roundToInt

object BicepCurlAnalysis : ExerciseAnalysis() {

    override fun getExercisePose(
        normalizedLm: MutableList<PointF3D>,
        side: String
    ): MutableMap<Int, Double> {
        // calculate joint angles
        val jointAngles = mutableMapOf<Int, Double>()

        when (side) {
            "right" -> {
                val rightElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_ELBOW],
                    normalizedLm[PoseLandmark.RIGHT_WRIST]
                )
                jointAngles[PoseLandmark.RIGHT_ELBOW] = rightElbowAngle

            }
            "left" -> {
                val leftElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_ELBOW],
                    normalizedLm[PoseLandmark.LEFT_WRIST]
                )
                jointAngles[PoseLandmark.LEFT_ELBOW] = leftElbowAngle

            }
            else -> {
                val rightElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_ELBOW],
                    normalizedLm[PoseLandmark.RIGHT_WRIST]
                )
                jointAngles[PoseLandmark.RIGHT_ELBOW] = rightElbowAngle

                val leftElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_ELBOW],
                    normalizedLm[PoseLandmark.LEFT_WRIST]
                )
                jointAngles[PoseLandmark.LEFT_ELBOW] = leftElbowAngle

                // upper arm trunk angle
                val rightUpperArmTrunk = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_ELBOW],
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_HIP]
                )

                val leftUpperArmTrunk = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_ELBOW],
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_HIP]
                )
                jointAngles[PoseLandmark.RIGHT_SHOULDER] = rightUpperArmTrunk
                jointAngles[PoseLandmark.LEFT_SHOULDER] = leftUpperArmTrunk

            }
        }

        return jointAngles
    }


    override fun analyseRep(jointAngles: Pair<Double, Double>): String {

        // TODO get starting position...
        // find min angle, max angle, and compare to thresholds

        val minElbowAngle = jointAngles.second
        val maxElbowAngle = jointAngles.first
        var feedback = ""

        feedback = if (minElbowAngle < 68 && maxElbowAngle >= 138) {
            "Correct"
        } else {
            "Wrong"
        }
        Log.i("Feedback", "elbowAngles=$jointAngles")

        Log.i("Feedback", "minAngle=$minElbowAngle maxAngle=$maxElbowAngle result=$feedback")
        return feedback
    }

    override fun analyseRepFront(
        leftJointAngles: MutableList<Double>,
        rightJointAngles: MutableList<Double>
    ): String {
        val rightMinElbowAngle = min(rightJointAngles).roundToInt()
        val leftMinElbowAngle = min(leftJointAngles).roundToInt()

        val rightMaxElbowAngle = max(rightJointAngles).roundToInt()
        val leftMaxElbowAngle = max(leftJointAngles).roundToInt()

        val feedback = if (rightMinElbowAngle < 60 && leftMinElbowAngle < 60
            && rightMaxElbowAngle >= 138 && leftMaxElbowAngle >= 138
        ) {
            "Correct"
        } else {
            "Wrong"
        }
        Log.i("Feedback", "leftAngles=$leftJointAngles")
        Log.i("Feedback", "rightAngles=$rightJointAngles")
        Log.i("Feedback", "leftMinAngle=$leftMinElbowAngle leftMaxAngle=$leftMaxElbowAngle " +
                "rightMinAngle=$rightMinElbowAngle rightMax=$rightMaxElbowAngle result=$feedback")

        return feedback
    }

}