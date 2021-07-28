package com.example.geofitapp.posedetection.poseDetector.exerciseProcessor

import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

abstract class ExerciseProcessor {
    abstract var lastRepResult: Int
    abstract var jointAnglesMap: MutableMap<Int, Double>
    abstract var pace: Float
    abstract var feedBack: String
    abstract var torso: Float?
    abstract var pose: List<PoseLandmark>?
    abstract var side: String

    abstract fun getRepCount(repCounter: ExerciseRepCounter): Triple<Int?, Float, MutableList<Double>?>
    abstract fun getFeedback(repAnalyzer: ExerciseAnalysis, repAngles: MutableList<Double>?): String
    abstract fun resetDetails()
    abstract fun initilizePose(
        exercise: String,
        pose: Pose,
        repCounter: ExerciseRepCounter,
        repAnalyzer: ExerciseAnalysis
    )
}