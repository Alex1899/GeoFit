package com.example.geofitapp.posedetection.poseDetector.exerciseProcessor

import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.FramePose
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

object BicepCurlProcessor : ExerciseProcessor() {
    override var lastRepResult = 0
    override var jointAnglesMap = mutableMapOf<Int, Double>()
    override var pace = 0f
    override var feedBack = mutableMapOf<Int, String>()
    override var torso: Float? = null
    override var pose: List<PoseLandmark>? = null
    override var side = ""
    override var finished = false
    override var repFinished: Boolean? = false

    var elbowAnglePairList = mutableMapOf<Pair<Double, Double>, MutableList<Double>>()
    var upperArmTrunkAnglePairList = mutableMapOf<Pair<Double, Double>, MutableList<Double>>()
    var trunkHipAnglePairList = mutableMapOf<Pair<Double, Double>, MutableList<Double>>()

    override fun getFeedback(repAnalyzer: ExerciseAnalysis) {
        val arr = elbowAnglePairList.keys.toList()
        feedBack[lastRepResult] = repAnalyzer.analyseRep(arr.last())
        Log.i("ProcessorKKK", "Feedback = $feedBack")
    }

    override fun resetDetails() {
        lastRepResult = 0
        pace = 0f
        feedBack.clear()
        side = ""
        repFinished = false
        finished = false
        elbowAnglePairList.clear()
    }


}