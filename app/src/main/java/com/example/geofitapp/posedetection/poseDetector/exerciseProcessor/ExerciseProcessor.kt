package com.example.geofitapp.posedetection.poseDetector.exerciseProcessor

import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

abstract class ExerciseProcessor {
    abstract var lastRepResult: Int
    abstract var jointAnglesMap: MutableMap<Int, Double>
    abstract var pace: Float
    abstract var exerciseFinishTime: Float
    abstract var feedBack: MutableMap<Int, MutableMap<String, MutableMap<String, Pair<String, String>>>>
    abstract var torso: Float?
    abstract var pose: List<PoseLandmark>?
    abstract var side: String
    abstract var finished: Boolean
    abstract var repFinished: Boolean?
    abstract var anglesOfInterest: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>
    abstract var allAnglesOfInterest: MutableMap<String, Triple<String, MutableList<Double>, Triple<Float, Float, Boolean>>>

    abstract fun getFeedback(repAnalyzer: ExerciseAnalysis)
    abstract fun getRepFormResult(): String
    abstract fun resetDetails()

}