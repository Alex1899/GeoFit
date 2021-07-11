package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose

abstract class ExerciseAnalysis {

    abstract fun getExercisePose(normalizedLm: MutableList<PointF3D>, side: String):  MutableMap<Int, Double>
    abstract fun analyseRep(jointAngles: MutableList<Double>): String
    abstract fun analyseRepFront(leftJointAngles: MutableList<Double>, rightJointAngles: MutableList<Double>): String
}