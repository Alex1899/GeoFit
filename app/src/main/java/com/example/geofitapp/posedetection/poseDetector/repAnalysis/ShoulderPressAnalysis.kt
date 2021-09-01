package com.example.geofitapp.posedetection.poseDetector.repAnalysis

import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs

object ShoulderPressAnalysis : ExerciseAnalysis() {
    override var side = ""
    override var elbowId: Int? = null
    override var shoulderId: Int? = null
    override var hipId: Int? = null

    // initilized in the getFramePose function

    private val startingPos = "Starting Position"
    private val middlePos = "Middle Position"
    private val finishingPos = "Finishing Position"


    override fun getStartingPositionFeedback(jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>): MutableMap<String, MutableMap<String, Triple<String, String, String>>> {
        val leftStartElbowAngle = jointAnglesMap[PoseLandmark.LEFT_ELBOW]!!.second[0]
        val rightStartElbowAngle = jointAnglesMap[PoseLandmark.RIGHT_ELBOW]!!.second[0]

        val leftStartShoulderAngle = jointAnglesMap[PoseLandmark.LEFT_SHOULDER]!!.second[0]
        val rightStartShoulderAngle = jointAnglesMap[PoseLandmark.RIGHT_SHOULDER]!!.second[0]

        val feedbackMap = mutableMapOf(startingPos to mutableMapOf<String, Triple<String, String, String>>())

        if (leftStartElbowAngle in 45.0..91.0 &&
            rightStartElbowAngle in 45.0..91.0
        ) {
            feedbackMap[startingPos]!!["Starting Elbow Angles"] = Triple(
                "Correct",
                "Good Form: Your forearms are positioned at correct angles to your upper arms",
                ""
            )
        } else {
            // left forearm
            if (45 > leftStartElbowAngle || leftStartElbowAngle > 91) {
                feedbackMap[startingPos]!!["Starting Elbow Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your left forearm is at the incorrect angle to your left upper arm.\n\n" +
                            "Fix: Try to have your forearm at approximately 90 degrees to your upper arm",
                    "Keep 90 degree angle at your elbows"

                )
            }

            // right forearm
            if (45 > rightStartElbowAngle || rightStartElbowAngle > 91) {
                feedbackMap[startingPos]!!["Starting Elbow Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your right forearm is at the incorrect angle to your right upper arm.\n\n" +
                            "Fix: Try to have your forearm at approximately 90 degrees to your upper arm",
                    "Keep 90 degree angle at your elbows"

                )
            }
        }

        if (leftStartShoulderAngle in 40.0..111.0 &&
            rightStartShoulderAngle in 40.0..111.0
        ) {
            feedbackMap[startingPos]!!["Starting Elbow to Torso Angles"] = Triple(
                "Correct",
                "Good Form: Your elbows are positioned correctly",
                ""
            )
        } else {
            // left elbow
            if (leftStartShoulderAngle > 111 || leftStartShoulderAngle < 40) {
                feedbackMap[startingPos]!!["Starting Elbow to Torso Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your left elbow is at the incorrect angle to your torso.\n\n" +
                            "Fix: Try to have your elbow at approximately 90 degrees to your torso",
                    "Keep elbows at 90 degrees to your torso"
                )
            }

            //right elbow
            if (rightStartShoulderAngle > 111 || rightStartShoulderAngle < 40) {
                feedbackMap[startingPos]!!["Starting Elbow to Torso Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your right elbow is at the incorrect angle to your torso.\n\n" +
                            "Fix: Try to have your elbow at approximately 90 degrees to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            }
        }


        return feedbackMap

    }

    override fun getMiddlePositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String, String>>> {

        val leftMinElbowAngle = jointAnglesMap[PoseLandmark.LEFT_ELBOW]!!.first.second
        val rightMinElbowAngle = jointAnglesMap[PoseLandmark.RIGHT_ELBOW]!!.first.second

        val leftMaxElbowAngle = jointAnglesMap[PoseLandmark.LEFT_ELBOW]!!.first.first
        val rightMaxElbowAngle = jointAnglesMap[PoseLandmark.RIGHT_ELBOW]!!.first.first

        val leftMinShoulderAngle = jointAnglesMap[PoseLandmark.LEFT_SHOULDER]!!.first.second
        val rightMinShoulderAngle = jointAnglesMap[PoseLandmark.RIGHT_SHOULDER]!!.first.second

        val leftMaxShoulderAngle = jointAnglesMap[PoseLandmark.LEFT_SHOULDER]!!.first.first
        val rightMaxShoulderAngle = jointAnglesMap[PoseLandmark.RIGHT_SHOULDER]!!.first.first

        if (!feedbackMap.containsKey(middlePos)) {
            feedbackMap[middlePos] = mutableMapOf()
        }
        // min elbow angles to check if forearm is close to shoulder
        if (leftMinElbowAngle in 45.0..91.0 && rightMinElbowAngle in 45.0..91.0) {
            if (abs(leftMinElbowAngle - rightMinElbowAngle) > 18) {
                if (leftMinElbowAngle > rightMinElbowAngle) {
                    feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                        "Wrong",
                        "Bad Form: Your right forearm is moved towards your right shoulder significantly.\n\nFix: Focus on having " +
                                "your forearms at equal distances from your shoulders in order to avoid muscular imbalances",
                        "Keep 90 degree angle at your elbows"
                    )
                } else {
                    feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                        "Wrong",
                        "Bad Form: Your left forearm is moved towards your left shoulder significantly.\n\nFix: Focus on having " +
                                "your forearms at equal distances from your shoulders in order to avoid muscular imbalances",
                        "Keep 90 degree angle at your elbows"
                    )
                }
            } else {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Correct",
                    "Good Form: Your forearms are almost perpendicular to your upper arms",
                    ""
                )
            }
        } else {
            //left
            if (leftMinElbowAngle < 45 && rightMinElbowAngle < 45) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your forearms showed significant movement " +
                            "towards your shoulders.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            } else if (leftMinElbowAngle < 45) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your left forearm showed significant movement " +
                            "towards left shoulder.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            } else if (rightMinElbowAngle < 45) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your right forearm showed significant movement " +
                            "towards right shoulder.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            } else if (leftMinElbowAngle > 91 && rightMinElbowAngle > 91) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your forearms are at the incorrect level to your upper arms.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            } else if (leftMinElbowAngle > 91) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your left forearm is at the incorrect level to your left upper arm.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            } else if (rightMinElbowAngle > 91) {
                feedbackMap[middlePos]!!["Min Forearm to Upper Arm Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Your right forearm is at the incorrect level to your right upper arm.\n\nFix: Try to not move your forearms significantly less than 90 " +
                            "degrees at your elbow joints",
                    "Keep 90 degree angle at your elbows"
                )
            }
        }

        // min elbow to shoulder angles
        if (leftMinShoulderAngle in 40.0..111.0 && rightMinShoulderAngle in 40.0..111.0) {
            if (abs(leftMinShoulderAngle - rightMinShoulderAngle) > 15) {
                if (leftMinShoulderAngle > rightMinShoulderAngle) {
                    feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                        "Wrong",
                        "Bad Form: Your right elbow is moved down significantly compared to your left.\n\nFix: Focus on having " +
                                "your elbows at equal angles to your torso in order to avoid muscular imbalances",
                        "Keep elbows at 90 degrees to your torso"
                    )
                } else {
                    feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                        "Wrong",
                        "Bad Form: Your left elbow is moved down significantly compared to your right.\n\nFix: Focus on having " +
                                "your elbows at equal angles to your torso in order to avoid muscular imbalances",
                        "Keep elbows at 90 degrees to your torso"

                    )
                }
            } else {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Correct",
                    "Good Form: Your upper arms are almost perpendicular to the torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            }
        } else {
            if (leftMinShoulderAngle < 40 && rightMinShoulderAngle < 40) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your elbows were at incorrect angles to your torso.\n\nFix: Focus on having " +
                            "your elbows almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            } else if (leftMinShoulderAngle < 40) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your left elbow is at incorrect angle to your torso.\n\nFix: Focus on having " +
                            "your elbows almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            } else if (rightMinShoulderAngle < 40) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your right elbow is at incorrect angle to your torso.\n\nFix: Focus on having " +
                            "your elbows almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            } else if (leftMinShoulderAngle > 111 && rightMinShoulderAngle > 111) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your elbows are too high.\n\nFix: Try to have your upper arms " +
                            "almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            } else if (leftMinShoulderAngle > 111) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your left elbow is too high.\n\nFix: Try to have your left upper arm " +
                            "almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            } else if (rightMinShoulderAngle > 111) {
                feedbackMap[middlePos]!!["Min Elbow Angles at the Bottom"] = Triple(
                    "Wrong",
                    "Bad Form: Your right elbow is too high.\n\nFix: Try to have your right upper arm " +
                            "almost perpendicular to your torso",
                    "Keep elbows at 90 degrees to your torso"

                )
            }
        }

        // top part
        if (leftMaxElbowAngle in 131.0..180.0 && rightMaxElbowAngle in 131.0..180.0 &&
            leftMaxShoulderAngle in 136.0..180.0 && rightMaxShoulderAngle in 136.0..180.0
        ) {
            if (abs(leftMaxShoulderAngle - rightMaxShoulderAngle) > 15) {
                if (leftMaxShoulderAngle > rightMinShoulderAngle) {
                    feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                        "Wrong",
                        "Bad Form: Your right arm is not fully extended.\n\nFix: Focus on extending your arms" +
                                " equally in order to avoid muscular imbalances.",
                        "Extend your arms more at the top"
                    )
                } else {
                    feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                        "Wrong",
                        "Bad Form: Your left arm is not fully extended.\n\nFix: Focus on extending your arms" +
                                " equally in order to avoid muscular imbalances.",
                        "Extend your arms more at the top"

                    )
                }
            } else {
                feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                    "Correct",
                    "Good Form: Your arms were extended correctly and the weight was brought up high enough",
                    ""
                )
            }

        } else {
            if (leftMaxElbowAngle < 131 && rightMaxElbowAngle < 131 &&
                leftMaxShoulderAngle < 136 && rightMaxShoulderAngle < 136
            ) {
                feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                    "Wrong",
                    "Bad Form: The weight was not brought up high enough. This could be because the weight is too heavy.\n\n" +
                            "Fix: Consider lowering the weight to target you shoulders better and avoid the risk of injury",
                    "Bring the weight up higher"
                )
            }else if (leftMaxElbowAngle < 131 && rightMaxElbowAngle < 131){
                feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                    "Wrong",
                    "Bad Form: Your elbow joints were not extended fully.\n\nFix: Focus on extending your" +
                            " elbows at the top part of the move to engage your shoulders more",
                    "Bring the weight up higher"

                )
            }else if(leftMaxElbowAngle < 131) {
                feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                    "Wrong",
                    "Bad Form: Your left arm was not extended correctly.\n\n" +
                            "Fix: Try to extend your arms without fully locking them out",
                    "Bring the weight up higher"

                )
            }else if (rightMaxElbowAngle < 131) {
                feedbackMap[middlePos]!!["Top Part of the Movement"] = Triple(
                    "Wrong",
                    "Bad Form: Your right arm was not extended correctly.\n\n" +
                            "Fix: Try to extend your arms without fully locking them out",
                    "Bring the weight up higher"

                )
            }
        }

        return feedbackMap

    }

    override fun getFinishingPositionFeedback(
        jointAnglesMap: MutableMap<Int, Pair<Pair<Double, Double>, MutableList<Double>>>,
        feedbackMap: MutableMap<String, MutableMap<String, Triple<String, String, String>>>
    ): MutableMap<String, MutableMap<String, Triple<String, String, String>>> {
        val leftFinishElbowAngle = jointAnglesMap[PoseLandmark.LEFT_ELBOW]!!.second.last()
        val rightFinishElbowAngle = jointAnglesMap[PoseLandmark.RIGHT_ELBOW]!!.second.last()

        val leftFinishShoulderAngle = jointAnglesMap[PoseLandmark.LEFT_SHOULDER]!!.second.last()
        val rightFinishShoulderAngle = jointAnglesMap[PoseLandmark.RIGHT_SHOULDER]!!.second.last()

        if (!feedbackMap.containsKey(finishingPos)) {
            feedbackMap[finishingPos] = mutableMapOf()
        }
        if (leftFinishElbowAngle in 45.0..91.0 &&
            rightFinishElbowAngle in 45.0..91.0
        ) {
            feedbackMap[finishingPos]!!["Finishing Elbow Angles"] = Triple(
                "Correct",
                "Good Form: Your forearms are positioned at correct angles to your upper arms",
                ""
            )
        } else {
            // left forearm
            if (45 > leftFinishElbowAngle || leftFinishElbowAngle > 91) {
                feedbackMap[finishingPos]!!["Finishing Elbow Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your left forearm is at the incorrect angle to your left upper arm.\n\n" +
                            "Fix: Try to have your forearm at approximately 90 degrees to your upper arm",
                    "Keep 90 degree angle at your elbows"
                )
            }

            // right forearm
            if (45 > rightFinishElbowAngle || rightFinishElbowAngle > 91) {
                feedbackMap[finishingPos]!!["Finishing Elbow Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect starting position. Your right forearm is at the incorrect angle to your right upper arm.\n\n" +
                            "Fix: Try to have your forearm at approximately 90 degrees to your upper arm",
                    "Keep 90 degree angle at your elbows"
                )
            }
        }

        if (leftFinishShoulderAngle in 40.0..111.0 &&
            rightFinishShoulderAngle in 40.0..111.0
        ) {
            feedbackMap[finishingPos]!!["Finishing Elbow to Torso Angles"] = Triple(
                "Correct",
                "Good Form: Your elbows are positioned correctly",
                ""

            )
        } else {
            // left elbow
            if (leftFinishShoulderAngle > 111 || leftFinishShoulderAngle < 40) {
                feedbackMap[finishingPos]!!["Finishing Elbow to Torso Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect finishing position. Your left elbow is at the incorrect angle to your torso.\n\n" +
                            "Fix: Try to have your elbow at approximately 90 degrees to your torso",
                    "Keep elbows at 90 degrees to your torso"
                )
            }

            //right elbow
            if (rightFinishShoulderAngle > 111 || rightFinishShoulderAngle < 40) {
                feedbackMap[finishingPos]!!["Finishing Elbow to Torso Angles"] = Triple(
                    "Wrong",
                    "Bad Form: Incorrect finishing position. Your right elbow is at the incorrect angle to your torso.\n\n" +
                            "Fix: Try to have your elbow at approximately 90 degrees to your torso",
                    "Keep elbows at 90 degrees to your torso"
                )
            }
        }

        return feedbackMap
    }
}