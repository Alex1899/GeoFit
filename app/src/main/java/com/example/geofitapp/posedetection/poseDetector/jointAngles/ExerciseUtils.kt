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
            "bicep_curl" to { normalizedLm: MutableList<PointF3D> -> BicepCurlAnalysis().getExercisePose(normalizedLm) }
        )
    val exerciseRepCounterAnalyzerMap = mutableMapOf(
        "bicep_curl" to Pair(BicepCurlRepCounter(), BicepCurlAnalysis())
    )


   fun convertToPoint3D(lm: List<PoseLandmark>): MutableList<PointF3D> {
       val point3d = mutableListOf<PointF3D>()
       for (landmark in lm){
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

    private fun detectSide(pose: Pose): String {
        val rightSide = listOf(
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_WRIST,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.RIGHT_KNEE,
            PoseLandmark.RIGHT_ANKLE
        )

        var rightSum = 0
        for (landmark in rightSide) {
            if (pose.getPoseLandmark(landmark)!!.inFrameLikelihood != 0.0f) {
                rightSum++
            }
        }

        val leftSide = listOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.LEFT_ELBOW,
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.LEFT_KNEE,
            PoseLandmark.LEFT_ANKLE
        )
        var leftSum = 0
        for (landmark in leftSide) {
            if (pose.getPoseLandmark(landmark)!!.inFrameLikelihood != 0.0f) {
                leftSum++
            }
        }

        return if (rightSum > leftSum) "right" else "left"
    }



    fun countReps(repCounter: ExerciseRepCounter, poseAnglesMap: MutableMap<Int, Double> ): Int? {
        val repsBefore: Int = repCounter.getTotalReps()
        val repsAfter: Int = repCounter.addNewFramePoseAngles(poseAnglesMap)
        var lastRepResult : Int? = null
        if (repsAfter > repsBefore) {
            // Play a fun beep when rep counter updates.
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP)
            lastRepResult = repsAfter
        }
        if(lastRepResult !== null){
            Log.i("PoseDetectorProcessor", "Rep: $lastRepResult")

        }
        return lastRepResult
    }

}