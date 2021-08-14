package com.example.geofitapp.posedetection.poseDetector.jointAngles


import android.util.Log
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.average
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.l2Norm2D
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.multiplyAll
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.subtract
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.subtractAll
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*

class JointAngles(
    private val exercise: String,
    private val side: String,
    private val torso: Float? = null
) {
    // Multiplier to apply to the torso to get minimal body size. Picked this by experimentation.
    private val TORSO_MULTIPLIER = 2.5f

    fun getFramePose(lm: MutableList<PointF3D>): MutableMap<Int, Double> {

        val normalizedLandmarks = if (side == "front") {
            normalizeFront(lm)
        } else {
            normalize(lm)
        }
        // get function from exercise name
        return AnalyzerUtils.getPose(exercise, normalizedLandmarks, side)
    }

    private fun normalize(lm: MutableList<PointF3D>): MutableList<PointF3D> {
        val normalizedLandmarks: MutableList<PointF3D> = ArrayList(lm)
        val hip = if (side == "right") {
            lm[PoseLandmark.RIGHT_HIP]
        } else {
            lm[PoseLandmark.LEFT_HIP]
        }
        subtractAll(hip, normalizedLandmarks)
        val poseSize = if (torso !== null) {
            torso
        } else {
            getPoseSize(normalizedLandmarks)
        }

        Log.i("Torso", "poseSize=$poseSize")
        // Normalize scale.
        multiplyAll(
            normalizedLandmarks,
            1 / poseSize
        )
        multiplyAll(normalizedLandmarks, 100f)
        return normalizedLandmarks
    }

    private fun normalizeFront(landmarks: MutableList<PointF3D>): MutableList<PointF3D> {
        val normalizedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
        // Normalize translation.
        val center: PointF3D = average(
            landmarks[PoseLandmark.LEFT_HIP], landmarks[PoseLandmark.RIGHT_HIP]
        )
        subtractAll(center, normalizedLandmarks)

        val poseSize = if (torso !== null) {
            torso
        } else {
            getPoseSizeFront(normalizedLandmarks)
        }

        Log.i("SidePose", "poseSize=$poseSize")
        // Normalize scale.
        multiplyAll(
            normalizedLandmarks,
            1 / poseSize
        )
        // Multiplication by 100 is not required, but makes it easier to debug.
        multiplyAll(normalizedLandmarks, 100f)
        return normalizedLandmarks
    }

    // IMPORTANT!! This needs to be called once
    // Translation normalization should've been done prior to calling this method.
    private fun getPoseSizeFront(landmarks: MutableList<PointF3D>): Float {
        // Note: This approach uses only 2D landmarks to compute pose size as using Z wasn't helpful
        // in our experimentation but you're welcome to tweak.
        val hipsCenter = average(
            landmarks[PoseLandmark.LEFT_HIP], landmarks[PoseLandmark.RIGHT_HIP]
        )
        val shouldersCenter = average(
            landmarks[PoseLandmark.LEFT_SHOULDER],
            landmarks[PoseLandmark.RIGHT_SHOULDER]
        )
        val torsoSize: Float = l2Norm2D(subtract(hipsCenter, shouldersCenter))
        var maxDistance: Float =
            torsoSize * TORSO_MULTIPLIER
        // torsoSize * TORSO_MULTIPLIER is the floor we want based on experimentation but actual size
        // can be bigger for a given pose depending on extension of limbs etc so we calculate that.
        for (landmark in landmarks) {
            val distance: Float = l2Norm2D(subtract(hipsCenter, landmark))
            if (distance > maxDistance) {
                maxDistance = distance
            }
        }

        PoseDetectorProcessor.torsoLengths.add(maxDistance)
        return maxDistance
    }

    fun getPoseSize(landmarks: MutableList<PointF3D>): Float {
        val hip: PointF3D
        val shoulder: PointF3D

        if (side == "right") {
            hip = landmarks[PoseLandmark.RIGHT_HIP]
            shoulder = landmarks[PoseLandmark.RIGHT_SHOULDER]
        } else {
            hip = landmarks[PoseLandmark.LEFT_HIP]
            shoulder = landmarks[PoseLandmark.LEFT_SHOULDER]
        }

        val torsoSize: Float = l2Norm2D(subtract(hip, shoulder))
        var maxDistance: Float =
            torsoSize * TORSO_MULTIPLIER

        for (landmark in landmarks) {
            val distance: Float = l2Norm2D(subtract(hip, landmark))
            if (distance > maxDistance) {
                maxDistance = distance
            }
        }

        PoseDetectorProcessor.torsoLengths.add(maxDistance)
        return maxDistance
    }
}