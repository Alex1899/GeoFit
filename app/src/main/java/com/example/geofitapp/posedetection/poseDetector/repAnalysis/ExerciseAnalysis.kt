package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose

abstract class ExerciseAnalysis {

    abstract fun getExercisePose(
        normalizedLm: MutableList<PointF3D>,
        side: String
    ): MutableMap<Int, Double>

    abstract fun analyseRep(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Pair<String, String>>>

    abstract fun getStartingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>>

    abstract fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>>

    abstract fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>>


}