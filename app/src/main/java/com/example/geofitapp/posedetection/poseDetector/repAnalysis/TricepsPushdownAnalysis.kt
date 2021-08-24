package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.common.PointF3D

object TricepsPushdownAnalysis : ExerciseAnalysis() {
    override var side = ""

    // initilized in the getFramePose function
    override var elbowId: Int? = null
    override var shoulderId: Int? = null
    override var hipId: Int? = null

    private val startingPos = "Starting Position"
    private val middlePos = "Middle Position"
    private val finishingPos = "Finishing Position"


    override fun getStartingPositionFeedback(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Triple<String,String, String>>> {
        val startElbowAngle = jointAnglesMap[elbowId]!!.second[0]
        val startShoulderAngle = jointAnglesMap[shoulderId]!!.second[0]
        val startHipAngle = jointAnglesMap[hipId]!!.second[0]

        val feedbackMap = mutableMapOf(startingPos to mutableMapOf<String, Triple<String,String, String>>())

        if (109 > startElbowAngle && startElbowAngle >= 45) {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Triple(
                "Correct",
                "Good Form: Correct starting position",
                ""
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position.\n\n" +
                        "Fix: Try to have your forearm at approximately 90 degrees to your upper arm",
                "Keep forearm perpendicular to the upper arm"
            )
        }

        if (startShoulderAngle < 24) {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Triple(
                "Correct",
                "Good Form: Elbows are positioned close to the torso",
                ""
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position. Your elbow showed significant movement forward.\n\nFix: " +
                        "Try to keep your elbows still and close to your torso throughout the movement.",
                "Keep elbow closer to your body"
            )
        }

        if (startHipAngle < 140 || startHipAngle > 195) {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Triple(
                "Wrong",
                "Bad Form: Incorrect starting position. Your torso showed significant movement.\nFix: " +
                        "Try to keep your torso still and straight throughout the movement",
                "Do not swing your body"
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
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String,String>>>
    ): MutableMap<String, MutableMap<String, Triple<String,String, String>>> {

        // elbow angle
        val maxElbowAngle = jointAnglesMap[elbowId]!!.first.first
        val minElbowAngle = jointAnglesMap[elbowId]!!.first.second

        // shoulder-trunk
        val maxShoulderAngle = jointAnglesMap[shoulderId]!!.first.first
//        val minShoulderAngle = jointAnglesMap[shoulderId]!!.first.second

        // hip angle
        val maxHipAngle = jointAnglesMap[hipId]!!.first.first
        val minHipAngle = jointAnglesMap[hipId]!!.first.second

        if (!feedbackMap.containsKey(middlePos)) {
            feedbackMap[middlePos] = mutableMapOf()
        }
        // elbow
        if (maxElbowAngle >= 155) {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Triple(
                "Correct",
                "Good Form: Your arms were fully extended at the bottom part of the move",
                ""
            )

        } else {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: You arms were not fully extended at the bottom part of the move.\n" +
                        "This could be because the weight is too heavy.\n\nFix: Consider lowering the weight " +
                        "to properly target your triceps and avoid the risk of injury. Focus on fully " +
                        "extending your arms at the bottom of the move to achieve more exertion " +
                        "on the triceps.",
                "Extend your arm fully"
            )

        }

        if (maxShoulderAngle < 24) {
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
                "Keep elbow closer to your body"

            )
        }

        if (minHipAngle > 140) {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Triple(
                "Correct",
                "Good Form: No leaning forward excessively",
                ""
            )
        } else {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Triple(
                "Wrong",
                "Bad Form: Leaning forward significantly. This could be because the weight is too heavy." +
                "This puts a lot of pressure on the lower back.\n\nFix: Consider lowering the weight." +
                        "Keep your back straight and focus the effort on the triceps only",
                "Do not swing your body"

            )
        }

        if (195 > maxHipAngle && maxHipAngle > 140) {
            feedbackMap[middlePos]!!["Maximum Angle at the Hip"] = Triple(
                "Correct",
                "Good Form: No leaning backwards excessively",
                ""
            )

        } else {
            feedbackMap[middlePos]!!["Maximum Angle at the Hip"] = Triple(
                "Wrong",
                "Bad Form: Leaning backwards significantly.This could be because the weight is too heavy." +
                        "This puts a lot of pressure on the lower back.\nFix: Consider lowering the weight." +
                        " Keep your back straight and focus the effort on the biceps only",
                "Do not swing your body"

            )
        }

        return feedbackMap
    }

    override fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String,String>>> {
        val finishElbowAngle = jointAnglesMap[elbowId]!!.second.last()
        val finishShoulderAngle = jointAnglesMap[shoulderId]!!.second.last()
        val finishHipAngle = jointAnglesMap[hipId]!!.second.last()

        if (!feedbackMap.containsKey(finishingPos)) {
            feedbackMap[finishingPos] = mutableMapOf()
        }

        if (finishElbowAngle >= 45 && 109 > finishElbowAngle) {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Triple(
                "Correct",
                "Good Form: Your arm was fully extended during the finishing part of the movement",
                ""

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Triple(
                "Wrong",
                "Bad Form: Incorrect finishing position. Your forearm is at incorrect angle to your upper arm.\n\nFix:" +
                        "Try to keep your forearm at approximately 90 degrees to your upper arm at the top part of the move",
                "Keep forearm perpendicular to the upper arm"
            )
        }

        if (finishShoulderAngle < 24) {
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
                "Keep elbow closer to your body"

            )
        }

        if (finishHipAngle >= 140 && 195 > finishHipAngle) {
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
                "Do not swing your body"
            )

        }

        return feedbackMap

    }
}