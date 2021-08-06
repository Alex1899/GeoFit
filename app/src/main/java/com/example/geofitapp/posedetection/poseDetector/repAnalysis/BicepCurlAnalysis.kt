package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark

object BicepCurlAnalysis : ExerciseAnalysis() {
    override var side = ""
    override var elbowId: Int? = null
    override var shoulderId: Int? = null
    override var hipId: Int? = null

    private val startingPos = "Starting Position"
    private val middlePos = "Middle Position"
    private val finishingPos = "Finishing Position"


    override fun getStartingPositionFeedback(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Pair<String, String>>> {
        val startElbowAngle = jointAnglesMap[elbowId]!!.second[0]
        val startShoulderAngle = jointAnglesMap[shoulderId]!!.second[0]
        val startHipAngle = jointAnglesMap[hipId]!!.second[0]

        val feedbackMap = mutableMapOf(startingPos to mutableMapOf<String, Pair<String, String>>())

        if (startElbowAngle >= 138) {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Pair(
                "Correct",
                ""
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your arm is not fully extended at the starting position" +
                        " of the move.\nFix: Focus on having your arms fully extended at the bottom part of the move"
            )
        }

        if (startShoulderAngle < 21) {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Pair(
                "Correct",
                "Good Form: Elbows are positioned close to the torso"
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your elbow showed significant movement forward.\nFix:" +
                        " Try to keep your elbows still and close to your torso throughout the movement.\n"
            )
        }

        if (startHipAngle < 165 || startHipAngle > 195) {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your torso showed significant movement.\nFix: " +
                        "Try to keep your torso still and straight throughout the movement"
            )
        } else {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No significant movement of the torso"
            )
        }

        return feedbackMap

    }

    override fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>> {

        // elbow angle
        val maxElbowAngle = jointAnglesMap[elbowId]!!.first.first
        val minElbowAngle = jointAnglesMap[elbowId]!!.first.second

        // shoulder-trunk
        val maxShoulderAngle = jointAnglesMap[shoulderId]!!.first.first
        val minShoulderAngle = jointAnglesMap[shoulderId]!!.first.second

        // hip angle
        val maxHipAngle = jointAnglesMap[hipId]!!.first.first
        val minHipAngle = jointAnglesMap[hipId]!!.first.second

        if (!feedbackMap.containsKey(middlePos)) {
            feedbackMap[middlePos] = mutableMapOf()
        }
        // elbow
        if (minElbowAngle < 68 && maxElbowAngle >= 138) {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Pair(
                "Correct",
                "Good Form: The weight was brought up high enough for a good contraction"
            )

        } else {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: The weight has not been curled high enough. This could be because the " +
                        "weight is too heavy.\nFix: Consider lowering the weight to properly target " +
                        "your biceps and avoid the risk of injury"
            )

        }

        if (maxShoulderAngle < 21) {
            feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] =
                Pair(
                    "Correct",
                    "Good Form: Elbows did not move significantly during the movement"

                )
        } else {
            feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Elbows have been shifted forward significantly.\nFix: Try to keep your elbows " +
                        "closer to your body"

            )
        }

        if (minHipAngle >= 165) {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No leaning forward excessively"

            )
        } else {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Leaning forward significantly.\nFix: Try to keep your back still and straight" +
                        " throughout the movement"

            )
        }

        if (195 > maxHipAngle && maxHipAngle >= 165) {
            feedbackMap[middlePos]!!["Maximum Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No leaning backwards excessively"
            )

        } else {
            feedbackMap[middlePos]!!["Maximum Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Leaning backwards significantly.This could be because the weight is too heavy." +
                        "This puts a lot of pressure on the lower back.\nFix: Consider lowering the weight." +
                        " Keep your back straight and focus the effort on the biceps only"

            )
        }

        return feedbackMap
    }

    override fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Pair<String, String>>>
    ): MutableMap<String, MutableMap<String, Pair<String, String>>> {
        val finishElbowAngle = jointAnglesMap[elbowId]!!.second.last()
        val finishShoulderAngle = jointAnglesMap[shoulderId]!!.second.last()
        val finishHipAngle = jointAnglesMap[hipId]!!.second.last()

        if (!feedbackMap.containsKey(finishingPos)) {
            feedbackMap[finishingPos] = mutableMapOf()
        }

        if (finishElbowAngle >= 138) {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Pair(
                "Correct",
                "Good Form: Weight lowered correctly"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. The weight was lowered half way.\nFix: Lower the weight until you achieve a " +
                        "correct finishing position"

            )
        }

        if (finishShoulderAngle < 21) {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Pair(
                "Correct",
                "Good Form: Elbow not moved significantly"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. Elbows have been shifted forward significantly.\nFix: Keep your elbows " +
                        "closer to your body at the bottom part of the move"

            )
        }

        if (finishHipAngle >= 165 && 195 > finishHipAngle) {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: Not significant movement of the torso"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. Your torso showed significant movement.\nFix:" +
                        " Try to keep your torso still and straight at the bottom part of the move"
            )

        }

        return feedbackMap

    }

}