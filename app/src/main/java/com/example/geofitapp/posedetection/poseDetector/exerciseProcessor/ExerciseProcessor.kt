package com.example.geofitapp.posedetection.poseDetector.exerciseProcessor

import com.example.geofitapp.posedetection.poseDetector.jointAngles.AnalyzerUtils
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.google.mlkit.vision.pose.PoseLandmark

object ExerciseProcessor {
    var lastRepResult = 0
    var jointAnglesMap = mutableMapOf<Int, Double>()
    var pace = 0f
    var exerciseFinishTime = 0f
    var feedBack =
        mutableMapOf<Int, MutableMap<String, MutableMap<String, Pair<String, String>>>>()
    var torso: Float? = null
    var pose: List<PoseLandmark>? = null
    var side = ""
    var finished = false
    var repFinished: Boolean? = false
    var anglesOfInterest =
        mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()

    var allAnglesOfInterest =
        mutableMapOf<String, Triple<String, MutableList<Double>, Triple<Float, Float, Boolean>>>()

    fun setAnglesOfInterestMap(map: MutableMap<String, Triple<String, MutableList<Double>, Triple<Float, Float, Boolean>>>) {
        allAnglesOfInterest = map
    }

    fun getFeedback(repAnalyzer: ExerciseAnalysis) {
        feedBack[lastRepResult] = AnalyzerUtils.analyseRep(
            anglesOfInterest, repAnalyzer
        )
    }

    fun getRepFormResult(): String {
        val map = feedBack.values.toList().last()
        for ((_, value) in map) {
            for ((_, v) in value) {
                if (v.first == "Wrong") {
                    return "Wrong"
                }
            }
        }
        return "Correct"
    }

    fun resetDetails() {
        lastRepResult = 0
        pace = 0f
        feedBack.clear()
        side = ""
        repFinished = false
        finished = false
        anglesOfInterest.clear()
        jointAnglesMap.clear()
        clearAllAnglesOfInterestMap()
    }

    private fun clearAllAnglesOfInterestMap(){
        for ((_, v) in allAnglesOfInterest){
            v.second.clear()
        }
    }

}