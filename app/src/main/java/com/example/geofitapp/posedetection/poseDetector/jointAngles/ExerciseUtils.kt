package com.example.geofitapp.posedetection.poseDetector.jointAngles

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.BicepCurlAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.FrontRaiseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ShoulderPressAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.TricepsPushdownAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.*
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.sqrt

object ExerciseUtils {

    val exerciseRepCounterAnalyzerMap = mutableMapOf(
        "Dumbbell Bicep Curl" to Pair(SideMaxMinRepCounter, BicepCurlAnalysis),
        "Triceps Pushdown" to Pair(SideMinMaxRepCounter, TricepsPushdownAnalysis),
        "Shoulder Press" to Pair(FrontExerciseRepCounter, ShoulderPressAnalysis),
        "Front Raise" to Pair(SideMinMaxRepCounter, FrontRaiseAnalysis)
    )

    val exerciseAnglesOfInterestMap =
        mutableMapOf<String, MutableMap<String, Triple<Pair<String, String?>, Pair<MutableList<Double>, MutableList<Double>? >, List<Triple<Float?, Float?, Boolean>>>>>(
            "Dumbbell Bicep Curl" to mutableMapOf(
                "elbow" to Triple(
                    Pair("Elbow angles", null),
                    Pair(
                        mutableListOf<Double>(),  null
                    ),
                    listOf(
                        Triple(138f, null, true),
                        Triple(null, 68f, true)
                    ) // Triple(maxAngleMax, maxAngleMin, cross = true or false)
                    // Triple(minAngleMax, minAngleMin, cross =true or false
                ),
                "shoulder" to Triple(
                    Pair("Elbow-torso angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(26f, null, false), Triple(null, 0f, false))
                ),
                "hip" to Triple(
                    Pair("Hip angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(195f, null, false), Triple(null, 168f, false))
                )
            ),
            "Triceps Pushdown" to mutableMapOf(
                "elbow" to Triple(
                    Pair("Elbow angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(155f, null, true), Triple(null, 109f, true))
                ),
                "shoulder" to Triple(
                    Pair("Elbow-torso angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(24f, null, false), Triple(null, 0f, false))
                ),
                "hip" to Triple(
                    Pair("Hip angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(195f, null, false), Triple(null, 139f, false))
                )
            ),
            // shoulder press
            "Shoulder Press" to mutableMapOf(
                "elbow" to Triple(
                    Pair("Left elbow angles", "Right elbow angles"),
                    Pair(
                        mutableListOf(), mutableListOf()
                    ),
                    listOf(Triple(130f, null, true), Triple(null, 44f, false))
                ),

                "shoulder" to Triple(
                    Pair(
                        "Left elbow-torso angles",
                        "Right elbow-torso angles"
                    ),
                    Pair(
                        mutableListOf(), mutableListOf()
                    ),
                    listOf(Triple(135f, null, true), Triple(null, 39f, false))
                ),
            ),

            // front raise
            "Front Raise" to mutableMapOf(
                "elbow" to Triple(
                    Pair("Elbow angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(181f, null, false), Triple(null, 148f, false))
                ),
                "shoulder" to Triple(
                    Pair("Elbow-torso angles", null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(122f, null, false), Triple(null, 67f, true))
                ),
                "hip" to Triple(
                    Pair("Hip angles",null),
                    Pair(
                        mutableListOf(), null
                    ),
                    listOf(Triple(195f, null, false), Triple(null, 174f, false))
                )
            )
        )

    // line chart y inverted boolean
    val isYaxisInverted = mutableMapOf(
        "Dumbbell Bicep Curl" to true,
        "Triceps Pushdown" to false,
        "Front Raise" to false,
        "Shoulder Press" to false
    )

    val mainAOIindexMap = mutableMapOf(
        "Dumbbell Bicep Curl" to mutableMapOf(
            "left" to listOf(PoseLandmark.LEFT_ELBOW),
            "right" to listOf(PoseLandmark.RIGHT_ELBOW)
        ),
        "Triceps Pushdown" to mutableMapOf(
            "left" to listOf(PoseLandmark.LEFT_ELBOW),
            "right" to listOf(PoseLandmark.RIGHT_ELBOW)
        ),
        "Front Raise" to mutableMapOf(
            "left" to listOf(PoseLandmark.LEFT_SHOULDER),
            "right" to listOf(PoseLandmark.RIGHT_SHOULDER)
        ),
        "Shoulder Press" to mutableMapOf(
            "front" to listOf(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
        )

    )

    fun getExerciseSide(exerciseName: String, pose: Pose):String {
        val exerciseSides = mainAOIindexMap[exerciseName]!!.keys

        if(!exerciseSides.contains("front")){
            return detectSide(pose)
        }

        return "front"
    }


    fun convertToPoint3D(lm: List<PoseLandmark>): MutableList<PointF3D> {
        val point3d = mutableListOf<PointF3D>()
        for (landmark in lm) {
            point3d.add(landmark.position3D)
        }
        return point3d
    }


    fun getAngle(firstPoint: PointF3D, midPoint: PointF3D, lastPoint: PointF3D): Double {
        var result = Math.toDegrees(
            kotlin.math.atan2(
                lastPoint.y.toDouble() - midPoint.y.toDouble(),
                lastPoint.x.toDouble() - midPoint.x.toDouble()
            )
                    - kotlin.math.atan2(
                firstPoint.y.toDouble() - midPoint.y.toDouble(),
                firstPoint.x.toDouble() - midPoint.x.toDouble()
            )
        )

        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }


    private fun detectSide(pose: Pose): String {
        val rightSide = listOf(
            PoseLandmark.RIGHT_EAR,
            PoseLandmark.RIGHT_EYE,
            PoseLandmark.RIGHT_MOUTH,
            PoseLandmark.RIGHT_EYE_OUTER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_WRIST,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.RIGHT_KNEE,
            PoseLandmark.RIGHT_ANKLE,
            PoseLandmark.RIGHT_PINKY,
            PoseLandmark.RIGHT_THUMB,
            PoseLandmark.RIGHT_INDEX,
        )

        var rightSum = 0.0f
        for (landmark in rightSide) {
            rightSum += pose.getPoseLandmark(landmark)!!.position3D.z

//            if(pose.getPoseLandmark(landmark)!!.inFrameLikelihood > 0.9f){
//                rightSum++
//            }
        }
        Log.i("InFrameLikelihood", "right ankle =${pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.inFrameLikelihood} z=${pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position3D?.z}")
        Log.i("InFrameLikelihood", "left ankle =${pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.inFrameLikelihood}  z=${pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position3D?.z}")


        val leftSide = listOf(
            PoseLandmark.LEFT_EAR,
            PoseLandmark.LEFT_EYE,
            PoseLandmark.LEFT_MOUTH,
            PoseLandmark.LEFT_EYE_OUTER,
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.LEFT_ELBOW,
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.LEFT_KNEE,
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.LEFT_PINKY,
            PoseLandmark.LEFT_THUMB,
            PoseLandmark.LEFT_INDEX,
        )
        var leftSum = 0.0f
        for (landmark in leftSide) {
            leftSum += pose.getPoseLandmark(landmark)!!.position3D.z

//            if (pose.getPoseLandmark(landmark)!!.position3D.z > 0.9f){
//                leftSum++
//            }

        }
//        val rightDistance = rightSum/13
//        val leftDistance = leftSum/13
//
        Log.i("DetectSideEx", "rightSum=${rightSum} leftSum=${leftSum}")

        var side = ""
        side = when {

            rightSum > leftSum -> {
                "left"
            }
            else -> {
                "right"
            }
        }

        return side
    }


    fun countReps(
        repCounter: ExerciseRepCounter,
        jointAnglesMap: MutableMap<Int, Double>,
        mainAOIindex: List<Int>
    ): ExerciseProcessor {
        val repsBefore: Int = repCounter.getTotalReps()
        val exerciseProcessor = repCounter.addNewFramePoseAngles(jointAnglesMap, mainAOIindex)
        if (exerciseProcessor.lastRepResult > repsBefore) {
            // Play a fun beep when rep counter updates.
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP)
        }

        return exerciseProcessor
    }


}