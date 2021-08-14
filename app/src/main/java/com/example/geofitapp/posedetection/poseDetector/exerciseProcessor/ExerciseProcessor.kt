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
        mutableMapOf<Int, MutableMap<String, MutableMap<String, Triple<String, String, String>>>>()
    var torso: Float? = null
    var pose: List<PoseLandmark>? = null
    var side = ""
    var finished = false
    var repFinished: Boolean? = false
    var anglesOfInterest =
        mutableMapOf<Int, Pair<Pair<Double, Double>, MutableList<Double>>>()

    var allAnglesOfInterest =
        mutableMapOf<String, Triple<Pair<String, String?>, Pair<MutableList<Double>, MutableList<Double>?>, List<Triple<Float?, Float?, Boolean>>>>()

    fun setAnglesOfInterestMap(map: MutableMap<String, Triple<Pair<String, String?>, Pair<MutableList<Double>, MutableList<Double>?>, List<Triple<Float?, Float?, Boolean>>>>) {
        allAnglesOfInterest = map
    }

    fun getFeedback(repAnalyzer: ExerciseAnalysis) {
        feedBack[lastRepResult] = AnalyzerUtils.analyseRep(
            anglesOfInterest, repAnalyzer
        )
    }

    fun getRepFormResult(): Pair<String, MutableList<String>> {
        val map = feedBack.values.toList().last()
        var stringArr= mutableListOf<String>()
        for ((_, value) in map) {
            for ((_, v) in value) {
                if (v.first == "Wrong") {
                    stringArr.add(v.third)
                }
            }

//            if(stringArr.size == 3) {
//                return Pair("Wrong", stringArr[0]+"," + stringArr[1] + "and " + stringArr[2])
//            }
//            if(stringArr.size == 2){
//                return Pair("Wrong", stringArr[0]+" and " + stringArr[1])
//            }
//            return Pair("Wrong", if(stringArr.isNotEmpty()) stringArr[0] else "")
        }

        stringArr = stringArr.distinct().toMutableList()
        return if(stringArr.isEmpty()) Pair("Correct", stringArr) else Pair("Wrong", stringArr)
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
            v.second.first.clear()
            v.second.second?.clear()

        }
    }

}