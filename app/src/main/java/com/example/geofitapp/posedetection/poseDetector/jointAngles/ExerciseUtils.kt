package com.example.geofitapp.posedetection.poseDetector.jointAngles

import android.media.AudioManager
import android.media.ToneGenerator
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.BicepCurlAnalysis
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.FrontRaiseAnalysis
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
        "Front Raise" to Pair(SideMinMaxRepCounter, FrontRaiseAnalysis)
    )

    val exerciseAnglesOfInterestMap = mutableMapOf(
        "Dumbbell Bicep Curl" to mutableMapOf(
            "elbow" to Triple(
                "Sequence of elbow angles",
                mutableListOf(),
                Triple(138f, 68f, false)
            ),
            "shoulder" to Triple(
                "Sequence of elbow shift angles",
                mutableListOf(),
                Triple(21f, 0f, true)
            ),
            "hip" to Triple(
                "Sequence of angles at the hip",
                mutableListOf<Double>(),
                Triple(195f, 165f, true)
            )
        ),
        "Triceps Pushdown" to mutableMapOf(
            "elbow" to Triple(
                "Sequence of elbow angles",
                mutableListOf(),
                Triple(150f, 109f, false)
            ),
            "shoulder" to Triple(
                "Sequence of elbow shift angles",
                mutableListOf(),
                Triple(24f, 0f, true)
            ),
            "hip" to Triple(
                "Sequence of angles at the hip",
                mutableListOf<Double>(),
                Triple(195f, 150f, true)
            )
        ),
        // shoulder press
        // front raise
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
            "left" to PoseLandmark.LEFT_ELBOW,
            "right" to PoseLandmark.RIGHT_ELBOW
        ),
        "Triceps Pushdown" to mutableMapOf(
            "left" to PoseLandmark.LEFT_ELBOW,
            "right" to PoseLandmark.RIGHT_ELBOW
        ),
        "Front Raise" to mutableMapOf(
            "left" to PoseLandmark.LEFT_SHOULDER,
            "right" to PoseLandmark.RIGHT_SHOULDER
        ),
        "Shoulder Press" to mutableMapOf(
            "left" to PoseLandmark.LEFT_ELBOW,
            "right" to PoseLandmark.RIGHT_ELBOW
        )

    )


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
//        var vect1 = listOf(firstPoint.x.toDouble() - midPoint.x.toDouble(), firstPoint.y.toDouble() - midPoint.y.toDouble(), firstPoint.z.toDouble() - midPoint.z.toDouble())
//        var vect2 = listOf(lastPoint.x.toDouble() - midPoint.x.toDouble(), lastPoint.y.toDouble() - midPoint.y.toDouble(), lastPoint.z.toDouble() - midPoint.z.toDouble())
//        vect1 = Utils.normVector(vect1)
//        vect2 = Utils.normVector(vect2)
//
//        var result = Math.toDegrees(angleBetweenVectors(vect1, vect2, 2))
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    fun magnitude(arr: List<Double>, N: Int): Double {

        // Stores the final magnitude
        var magnitude = 0.0

        // Traverse the array
        for (i in 0 until N) magnitude += arr[i] * arr[i]

        // Return square root of magnitude
        return sqrt(magnitude)
    }

    // Function to find the dot
    // product of two vectors
    fun dotProduct(
        arr: List<Double>,
        brr: List<Double>, N: Int
    ): Double {

        // Stores dot product
        var product = 0.0

        // Traverse the array
        for (i in 0 until N) product += arr[i] * brr[i]

        // Return the product
        return product
    }

    fun angleBetweenVectors(
        arr: List<Double>,
        brr: List<Double>,
        N: Int
    ): Double {

        // Stores dot product of two vectors
        val dotProductOfVectors = dotProduct(arr, brr, N)

        // Stores magnitude of vector A
        val magnitudeOfA = magnitude(arr, N)

        // Stores magnitude of vector B
        val magnitudeOfB = magnitude(brr, N)

        // Stores angle between given vectors

        return kotlin.math.acos(
            dotProductOfVectors /
                    (magnitudeOfA * magnitudeOfB)
        )
    }

    fun detectSide(pose: Pose): String {
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
//        Log.i("Reps", "rightSum=${rightDistance} leftSum=${leftDistance}")

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
        mainAOIindex: Int
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