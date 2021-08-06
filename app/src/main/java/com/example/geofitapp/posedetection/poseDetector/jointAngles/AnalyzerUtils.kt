package com.example.geofitapp.posedetection.poseDetector.jointAngles

import com.example.geofitapp.posedetection.poseDetector.repAnalysis.BicepCurlAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.TricepsPushdownAnalysis
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark

object AnalyzerUtils {

    private val exerciseAnalysisMap = mutableMapOf(
        "Dumbbell Bicep Curl" to BicepCurlAnalysis,
        "Triceps Pushdown" to TricepsPushdownAnalysis
    )

    fun getPose(
        exercise: String,
        normalizedLm: MutableList<PointF3D>,
        side: String,
    ): MutableMap<Int, Double> {
        return getExercisePose(
            normalizedLm, side,
            exerciseAnalysisMap[exercise]!!
        )
    }

    private fun getExercisePose(
        normalizedLm: MutableList<PointF3D>,
        side: String,
        exerciseAnalysis: ExerciseAnalysis
    ): MutableMap<Int, Double> {
        exerciseAnalysis.side = side
        // calculate joint angles
        val jointAngles = mutableMapOf<Int, Double>()

        when (side) {
            "right" -> {
                exerciseAnalysis.elbowId = PoseLandmark.RIGHT_ELBOW
                exerciseAnalysis.shoulderId = PoseLandmark.RIGHT_SHOULDER
                exerciseAnalysis.hipId = PoseLandmark.RIGHT_HIP

                val rightElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_ELBOW],
                    normalizedLm[PoseLandmark.RIGHT_WRIST]
                )

                val rightShoulderAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_ELBOW],
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_HIP]
                )

                val rightHipAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.RIGHT_SHOULDER],
                    normalizedLm[PoseLandmark.RIGHT_HIP],
                    normalizedLm[PoseLandmark.RIGHT_KNEE]
                )
                jointAngles[PoseLandmark.RIGHT_WRIST] = 0.0
                jointAngles[exerciseAnalysis.elbowId!!] = rightElbowAngle
                jointAngles[exerciseAnalysis.shoulderId!!] = rightShoulderAngle
                jointAngles[exerciseAnalysis.hipId!!] = rightHipAngle
            }
            else -> {
                exerciseAnalysis.elbowId = PoseLandmark.LEFT_ELBOW
                exerciseAnalysis.shoulderId = PoseLandmark.LEFT_SHOULDER
                exerciseAnalysis.hipId = PoseLandmark.LEFT_HIP

                val leftElbowAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_ELBOW],
                    normalizedLm[PoseLandmark.LEFT_WRIST]
                )

                val leftShoulderAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_ELBOW],
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_HIP]
                )

                val leftHipAngle = ExerciseUtils.getAngle(
                    normalizedLm[PoseLandmark.LEFT_SHOULDER],
                    normalizedLm[PoseLandmark.LEFT_HIP],
                    normalizedLm[PoseLandmark.LEFT_KNEE]
                )
                // add wrist to draw full pose in poseGraphic
                jointAngles[PoseLandmark.LEFT_WRIST] = 0.0
                jointAngles[exerciseAnalysis.elbowId!!] = leftElbowAngle
                jointAngles[exerciseAnalysis.shoulderId!!] = leftShoulderAngle
                jointAngles[exerciseAnalysis.hipId!!] = leftHipAngle

            }

        }

        return jointAngles
    }

    fun analyseRep(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        exerciseAnalysis: ExerciseAnalysis
    ): MutableMap<String, MutableMap<String, Pair<String, String>>> {

        // starting position
        val startingFeedback = exerciseAnalysis.getStartingPositionFeedback(jointAnglesMap)
        // middle position
        val middleFeedback =
            exerciseAnalysis.getMiddlePositionFeedback(jointAnglesMap, startingFeedback)
        // finishing position
        return exerciseAnalysis.getFinishingPositionFeedback(jointAnglesMap, middleFeedback)
    }
}