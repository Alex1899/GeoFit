package com.example.geofitapp.posedetection.poseDetector.exerciseProcessor

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
    override var feedBack = ""
    override var torso: Float? = null
    override var pose: List<PoseLandmark>? = null
    override var side = ""


    override fun initilizePose(
        exercise: String,
        pose: Pose,
        repCounter: ExerciseRepCounter,
        repAnalyzer: ExerciseAnalysis
    ) {
        if(pose.allPoseLandmarks.isEmpty()){
            return
        }
        this.pose = pose.allPoseLandmarks
        side = "right" //ExerciseUtils.detectSide(pose)

        jointAnglesMap =
            FramePose(exercise,side).getFramePose(ExerciseUtils.convertToPoint3D(pose.allPoseLandmarks))

        val triple = getRepCount(repCounter)
        lastRepResult = triple.first ?: lastRepResult
        pace = triple.second
        feedBack = getFeedback(repAnalyzer, triple.third)
    }

    override fun getRepCount(repCounter: ExerciseRepCounter):  Triple<Int?, Float, MutableList<Double>?>{
        return ExerciseUtils.countReps(repCounter, jointAnglesMap, side)
    }


    override fun getFeedback(repAnalyzer: ExerciseAnalysis, repAngles: MutableList<Double>?): String {
        if(repAngles == null){
            return ""
        }
        return repAnalyzer.analyseRep(repAngles)
    }

    override fun resetDetails() {
        lastRepResult = 0
        jointAnglesMap = mutableMapOf()
        pace = 0f
        feedBack = ""
        torso = null
        pose = null
        side = ""
    }


}