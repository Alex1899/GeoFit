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
    override var exerciseFinishTime = 0f
    override var feedBack =
        mutableMapOf<Int, List<MutableMap<String, MutableMap<String, Pair<String, String>>>>>()
    override var torso: Float? = null
    override var pose: List<PoseLandmark>? = null
    override var side = ""
    override var finished = false
    override var repFinished: Boolean? = false
    override var anglesOfInterest =
        mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()

    override var allAnglesOfInterest = mutableMapOf(
        "elbow" to Triple("Sequence of elbow angles", mutableListOf(), Triple(138f, 68f, false)),
        "shoulder" to Triple("Sequence of elbow shift angles", mutableListOf(), Triple(21f, 0f, true)),
        "hip" to Triple("Sequence of angles at the hip", mutableListOf<Double>(), Triple(195f, 165f, true)),

        )


//    var elbowAnglePairList = mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()
//    var upperArmTrunkAnglePairList = mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()
//    var trunkHipAnglePairList = mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()

    override fun getFeedback(repAnalyzer: ExerciseAnalysis) {
        feedBack[lastRepResult] = repAnalyzer.analyseRep(anglesOfInterest)
        Log.i("ProcessorKKK", "Feedback = $feedBack")
    }

    override fun getRepFormResult(): String {
        var feedback = ""
        val list = feedBack.values.toList().last()
        for (map in list) {
            for ((_, value) in map) {
                for ((_, v) in value) {
                    if (v.first == "Wrong") {
                        return "Wrong"
                    }
                }
            }
        }
        return "Correct"
    }

    override fun resetDetails() {
        lastRepResult = 0
        pace = 0f
        feedBack.clear()
        side = ""
        repFinished = false
        finished = false
        anglesOfInterest.clear()
    }


}