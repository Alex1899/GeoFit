package com.example.geofitapp.posedetection.poseDetector.repAnalysis

object FrontRaiseAnalysis : ExerciseAnalysis() {
    override var side = ""
    // initilized in the getFramePose function
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

        if (181 > startElbowAngle && startElbowAngle >= 148) {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Pair(
                "Correct",
                "Good Form: Your arm is fully extended."
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your arm is not fully extended." +
                        "\n\nFix: Try to have your elbows fully extended throughout the movement."
            )
        }

        if (15 > startShoulderAngle && startShoulderAngle >= 0.02) {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Pair(
                "Correct",
                "Good Form: Elbow is positioned close to the torso."
            )
        } else {
            feedbackMap[startingPos]!!["Starting Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your elbow showed significant movement.\n\nFix: " +
                        "Try to keep your elbows still and close to your torso at the starting position of the move.'"
            )
        }

        if (startHipAngle >= 154 && startHipAngle < 195) {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No significant movement of the torso"
            )
        } else {
            feedbackMap[startingPos]!!["Starting Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Incorrect starting position. Your torso showed significant movement.\n\nFix: " +
                        "Try to keep your torso still and straight throughout the movement.'"
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
        if (minElbowAngle < 181 && minElbowAngle >= 148) {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Pair(
                "Correct",
                "Good Form: Your forearm is at the correct angle to your upper arm"
            )

        } else {
            feedbackMap[middlePos]!!["Minimum Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: Your elbow is not extended enough.\n\nFix: Focus on having your elbows extended" +
                        " throughout the movement for a better front deltoids contraction"
            )

        }

        if (69 <= maxShoulderAngle && maxShoulderAngle < 122) {
            feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] =
                Pair(
                    "Correct",
                    "Good Form: The weight was brought up high enough"
                )
        } else {
            if (maxShoulderAngle < 69) {
                feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] = Pair(
                    "Wrong",
                    "Bad Form: The weight has not been brought up high enough. This could be because the weight" +
                            " is too heavy.\n\nFix: Consider lowering the weight to properly target your front deltoids " +
                            "and avoid the risk of injury"
                )
            }
            if (maxShoulderAngle > 122) {
                feedbackMap[middlePos]!!["Maximum Elbow Forward Shift"] = Pair(
                    "Wrong",
                    "Bad Form: The weight has been brought up way too much.\n\nFix: Try not to bring the weight" +
                            " higher than your shoulder level in order to keep the tension on front deltoids"
                )
            }
        }

        if (15 > minShoulderAngle && minShoulderAngle >= 0.01) {
            feedbackMap[middlePos]!!["Minimum Elbow Forward Shift"] = Pair(
                "Correct",
                "Good Form: Elbow return to the correct position"
            )
        } else {
            feedbackMap[middlePos]!!["Minimum Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Incorrect elbow position. The weight was lowered halfway.\n\nFix: Focus on lowering" +
                        "the weight fully at the bottom part of the move"
            )
        }

        if (minHipAngle >= 154) {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No leaning forward excessively"

            )
        } else {
            feedbackMap[middlePos]!!["Minimum Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Leaning forward significantly.\n\nFix: Try to keep your back still and straight" +
                        " throughout the movement"
            )
        }

        if (195 > maxHipAngle && maxHipAngle >= 154) {
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

        if (finishElbowAngle >= 148 && 181 > finishElbowAngle) {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Pair(
                "Correct",
                "Good Form: Your arm was fully extended during the finishing part of the movement"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Angle"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. Your forearm is at incorrect angle to your upper arm.\n\nFix:" +
                        "Try to extend your arms at the bottom part of the move"
            )
        }

        if (finishShoulderAngle < 15 && finishShoulderAngle >= 0.01) {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Pair(
                "Correct",
                "Good Form: Correct finishing position of the elbow"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Elbow Forward Shift"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. Elbows have been shifted forward significantly.\\n\nFix: Keep your elbows" +
                        " closer to your body at the bottom part of the move."

            )
        }

        if (finishHipAngle >= 154 && 195 > finishHipAngle) {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Pair(
                "Correct",
                "Good Form: No significant movement of the torso"

            )
        } else {
            feedbackMap[finishingPos]!!["Finishing Angle at the Hip"] = Pair(
                "Wrong",
                "Bad Form: Incorrect finishing position. Your torso showed significant movement.\n\nFix:" +
                        " Try to keep your torso still and straight at the bottom part of the move"
            )

        }

        return feedbackMap

    }
}