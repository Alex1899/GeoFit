package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D

object TricepsPushdownAnalysis: ExerciseAnalysis() {
    override var side = ""
    override var elbowId: Int? = null
    override var shoulderId: Int? = null
    override var hipId: Int? = null

    private val startingPos = "Starting Position"
    private val middlePos = "Middle Position"
    private val finishingPos = "Finishing Position"


    override fun getStartingPositionFeedback(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Pair<String, String>>> {
        TODO("Not yet implemented")
    }

    override fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>> {
        TODO("Not yet implemented")
    }

    override fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>> {
        TODO("Not yet implemented")
    }
}