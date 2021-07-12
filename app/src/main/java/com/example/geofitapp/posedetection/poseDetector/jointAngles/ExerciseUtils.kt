package com.example.geofitapp.posedetection.poseDetector.jointAngles

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.BicepCurlAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.BicepCurlRepCounter
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*
import kotlin.math.abs

object ExerciseUtils {
    val exerciseAnglesMap =
        mutableMapOf(
            "Dumbbell Bicep Curl" to { normalizedLm: MutableList<PointF3D>, side: String ->
                BicepCurlAnalysis().getExercisePose(
                    normalizedLm,
                    side
                )
            }
        )
    val exerciseRepCounterAnalyzerMap = mutableMapOf(
        "Dumbbell Bicep Curl" to Pair(BicepCurlRepCounter(), BicepCurlAnalysis())
    )


    fun convertToPoint3D(lm: List<PoseLandmark>): MutableList<PointF3D> {
        val point3d = mutableListOf<PointF3D>()
        for (landmark in lm) {
            point3d.add(landmark.position3D)
        }
        return point3d
    }

    fun getAngle(firstPoint: PointF3D, midPoint: PointF3D, lastPoint: PointF3D): Double {
        var result = Math.toDegrees(
            kotlin.math.atan2(
                lastPoint.y.toDouble() - midPoint.y.toDouble(),
                lastPoint.x.toDouble() - midPoint.x.toDouble()
            )
                    - kotlin.math.atan2(
                firstPoint.y.toDouble() - midPoint.y.toDouble(),
                firstPoint.x.toDouble() - midPoint.x.toDouble()
            )
        )
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    fun detectSide(pose: Pose): String {
        val rightSide = listOf(
            PoseLandmark.RIGHT_EAR,
            PoseLandmark.RIGHT_EYE,
            PoseLandmark.RIGHT_MOUTH,
            PoseLandmark.RIGHT_EYE_OUTER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_WRIST,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.RIGHT_KNEE,
            PoseLandmark.RIGHT_ANKLE,
            PoseLandmark.RIGHT_PINKY,
            PoseLandmark.RIGHT_THUMB,
            PoseLandmark.RIGHT_INDEX,
            )

        var rightSum = 0.0f
        for (landmark in rightSide) {
            rightSum += pose.getPoseLandmark(landmark)!!.position3D.z

//            if(pose.getPoseLandmark(landmark)!!.inFrameLikelihood > 0.9f){
//                rightSum++
//            }
        }

        val leftSide = listOf(
            PoseLandmark.LEFT_EAR,
            PoseLandmark.LEFT_EYE,
            PoseLandmark.LEFT_MOUTH,
            PoseLandmark.LEFT_EYE_OUTER,
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.LEFT_ELBOW,
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.LEFT_KNEE,
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.LEFT_PINKY,
            PoseLandmark.LEFT_THUMB,
            PoseLandmark.LEFT_INDEX,
        )
        var leftSum = 0.0f
        for (landmark in leftSide) {
            leftSum += pose.getPoseLandmark(landmark)!!.position3D.z

//            if (pose.getPoseLandmark(landmark)!!.position3D.z > 0.9f){
//                leftSum++
//            }

        }
        val rightDistance = rightSum/13
        val leftDistance = leftSum/13

        Log.i("Reps", "rightSum=${rightDistance} leftSum=${leftDistance}")

        var side = ""
        side = when {
            rightDistance < 0 && leftDistance < 0 -> {
                "front"
            }
            rightDistance > leftDistance -> {
                "left"
            }
            rightDistance < leftDistance -> {
                "right"
            }
            else -> {
                "front"
            }
        }

        return side
    }


    fun countReps(
        repCounter: ExerciseRepCounter,
        jointAnglesMap: MutableMap<Int, Double>,
        side: String
    ): Int? {
        val repsBefore: Int = repCounter.getTotalReps()
        val repsAfter: Int = repCounter.addNewFramePoseAngles(jointAnglesMap, side)
        var lastRepResult: Int? = null
        if (repsAfter > repsBefore) {
            // Play a fun beep when rep counter updates.
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP)
            lastRepResult = repsAfter
        }
        if (lastRepResult !== null) {
            Log.i("PoseDetectorProcessor", "Rep: $lastRepResult")

        }
        return lastRepResult
    }

}