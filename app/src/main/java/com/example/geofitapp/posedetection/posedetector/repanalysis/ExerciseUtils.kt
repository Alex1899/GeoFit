package com.example.geofitapp.posedetection.posedetector.repanalysis

import android.graphics.Point
import android.util.Log
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.ArrayList
import kotlin.math.abs

object ExerciseUtils {
    val exerciseAnglesMap =
        mutableMapOf(
            "bicep_curl" to { normalizedLm: MutableList<PointF3D>, lm: MutableList<PointF3D>-> getBicepCurlPose(normalizedLm, lm) }
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

    private fun getBicepCurlPose(normalizedLm: MutableList<PointF3D>, lm: MutableList<PointF3D>): MutableList<Pair<PointF3D, Double>> {
        // calculate joint angles
        val jointAngles = mutableListOf<Pair<PointF3D, Double>>()
        val rightElbowAngle = getAngle(
            normalizedLm[PoseLandmark.RIGHT_SHOULDER],
            normalizedLm[PoseLandmark.RIGHT_ELBOW],
            normalizedLm[PoseLandmark.RIGHT_WRIST]
        )
        val leftElbowAngle = getAngle(
            normalizedLm[PoseLandmark.LEFT_SHOULDER],
            normalizedLm[PoseLandmark.LEFT_ELBOW],
            normalizedLm[PoseLandmark.LEFT_WRIST]
        )
        jointAngles.add(Pair(lm[PoseLandmark.RIGHT_ELBOW], rightElbowAngle))
        jointAngles.add(Pair(lm[PoseLandmark.LEFT_ELBOW], leftElbowAngle))

        Log.i("ExercisUtil", "joint angles ${jointAngles}")

        return jointAngles

    }

}