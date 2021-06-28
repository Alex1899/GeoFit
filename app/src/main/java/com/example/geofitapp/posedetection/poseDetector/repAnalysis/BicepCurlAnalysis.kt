package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.roundToInt

class BicepCurlAnalysis: ExerciseAnalysis() {

    override fun getExercisePose(normalizedLm: MutableList<PointF3D>): MutableMap<Int, Double> {
        // calculate joint angles
        val jointAngles = mutableMapOf<Int, Double>()
        val rightElbowAngle = ExerciseUtils.getAngle(
            normalizedLm[PoseLandmark.RIGHT_SHOULDER],
            normalizedLm[PoseLandmark.RIGHT_ELBOW],
            normalizedLm[PoseLandmark.RIGHT_WRIST]
        )
        val leftElbowAngle = ExerciseUtils.getAngle(
            normalizedLm[PoseLandmark.LEFT_SHOULDER],
            normalizedLm[PoseLandmark.LEFT_ELBOW],
            normalizedLm[PoseLandmark.LEFT_WRIST]
        )
        jointAngles[PoseLandmark.RIGHT_ELBOW] = rightElbowAngle
        jointAngles[PoseLandmark.LEFT_ELBOW] = leftElbowAngle

        return jointAngles
    }

    override fun analyseRep(jointAngles: MutableList<Double>): String {
        // find min angle, max angle, and compare to thresholds
        val minElbowAngle = min(jointAngles).roundToInt()
        val maxElbowAngle = max(jointAngles).roundToInt()
        var feedback = ""

        feedback = if(minElbowAngle < 68 && maxElbowAngle >= 138){
            "Correct"
        }else{
            "Wrong"
        }
        return feedback
    }

}