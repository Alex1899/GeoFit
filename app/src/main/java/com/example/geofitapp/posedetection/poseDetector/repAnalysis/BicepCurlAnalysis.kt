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


    override fun getStartingPositionFeedback(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Triple<String, String, String>>> {
        val startElbowAngle = jointAnglesMap[elbowId]!!.second[0]
        val startShoulderAngle = jointAnglesMap[shoulderId]!!.second[0]
        val startHipAngle = jointAnglesMap[hipId]!!.second[0]

        val feedbackMap = mutableMapOf(startingPos to mutableMapOf<String, Triple<String, String,  String>>())

        if (startElbowAngle >= 138) {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Triple(
                "Correct",
                "Good Form: Your arm is fully extended at the starting position",
                ""
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position. Your arm is not fully extended at the starting position" +
                        " of the move.\nFix: Focus on having your arms fully extended at the bottom part of the move",
                "Extend your arms fully",

                )
        }

        if (startShoulderAngle < 26) {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Triple(
                "Correct",
                "Good Form: Elbows are positioned close to the torso",
                ""
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position. Your elbow showed significant movement forward.\nFix:" +
                        " Try to keep your elbows still and close to your torso throughout the movement.\n",
                "Keep elbows closer to your body"
            )
        }

        if (startHipAngle < 165 || startHipAngle > 195) {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position. Your torso showed significant movement.\nFix: " +
                        "Try to keep your torso still and straight throughout the movement",
                "Keep your back still"
            )
        } else {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Triple(
                "Correct",
                "Good Form: No significant movement of the torso",
                ""
            )
        }

        return feedbackMap

    }

    override fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String,String, String>>> {

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
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Triple(
                "Correct",
                "Good Form: The weight was brought up high enough for a good contraction",
                ""
            )

        } else {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: The weight has not been curled high enough. This could be because the " +
                        "weight is too heavy.\nFix: Consider lowering the weight to properly target " +
                        "your biceps and avoid the risk of injury",
                "Curl the weight higher"
            )

        }

        if (maxShoulderAngle < 26) {
            feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] =
                Triple(
                    "Correct",
                    "Good Form: Elbows did not move significantly during the movement",
                    ""

                )
        } else {
            feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] = Triple(
                "Wrong",
                "Bad Form: Elbows have been shifted forward significantly.\nFix: Try to keep your elbows " +
                        "closer to your body",
                "Keep elbows closer to your body"

            )
        }

        if (minHipAngle > 168) {
            feedbackMap[middlePos]!!["Back Movement"] = Triple(
                "Correct",
                "Good Form: Your back is still and straight during the exercise",
                ""

            )
        } else {
            feedbackMap[middlePos]!!["Back Movement"] = Triple(
                "Wrong",
                "Bad Form: Your back is not straight.\nFix: Try to keep your back still" +
                        " throughout the movement",
                "Keep your back still"

            )
        }


        return feedbackMap
    }

    override fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String,String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String,String, String>>> {
        val finishElbowAngle = jointAnglesMap[elbowId]!!.second.last()
        val finishShoulderAngle = jointAnglesMap[shoulderId]!!.second.last()
        val finishHipAngle = jointAnglesMap[hipId]!!.second.last()

        if (!feedbackMap.containsKey(finishingPos)) {
            feedbackMap[finishingPos] = mutableMapOf()
        }

        if (finishElbowAngle >= 138) {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Triple(
                "Correct",
                "Good Form: Weight lowered correctly",
                ""

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: Incorrect finishing position. The weight was lowered half way.\nFix: Lower the weight until you achieve a " +
                        "correct finishing position",
                "Extend your arms fully"

            )
        }

        if (finishShoulderAngle < 26    ) {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Triple(
                "Correct",
                "Good Form: Elbow not moved significantly",
                ""

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Triple(
                "Wrong",
                "Bad Form: Incorrect finishing position. Elbows have been shifted forward significantly.\nFix: Keep your elbows " +
                        "closer to your body at the bottom part of the move",
                "Keep elbows closer to your body"

            )
        }

        if (finishHipAngle >= 165 && 195 > finishHipAngle) {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Triple(
                "Correct",
                "Good Form: Not significant movement of the torso",
                ""

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Triple(
                "Wrong",
                "Bad Form: Incorrect finishing position. Your torso showed significant movement.\nFix:" +
                        " Try to keep your torso still and straight at the bottom part of the move",
                "Keep your back still"
            )

        }

        return feedbackMap

    }

}