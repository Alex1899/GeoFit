package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D

abstract class ExerciseAnalysis {

    abstract fun getExercisePose(normalizedLm: MutableList<PointF3D>):  MutableMap<Int, Double>
    abstract fun analyseRep(jointAngles: MutableList<Double>): String
}