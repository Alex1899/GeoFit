package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose

abstract class ExerciseAnalysis {
    abstract var side: String
    abstract var elbowId: Int?
    abstract var shoulderId: Int?
    abstract var hipId: Int?


    abstract fun getStartingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String, String>>>

    abstract fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String, String>>>

    abstract fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String, String>>>


}