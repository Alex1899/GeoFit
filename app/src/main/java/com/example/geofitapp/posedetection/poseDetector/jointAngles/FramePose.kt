package com.example.geofitapp.posedetection.poseDetector.jointAngles


import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.average
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.l2Norm2D
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.multiplyAll
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.subtract
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils.subtractAll
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*

class FramePose(private val exercise: String) {
    // Multiplier to apply to the torso to get minimal body size. Picked this by experimentation.
    private val TORSO_MULTIPLIER = 2.5f

    fun getFramePose(lm: MutableList<PointF3D>): MutableMap<Int, Double> {
        val normalizedLandmarks: MutableList<PointF3D> = normalize(lm)
        // get function from exercise name
        val getPose = ExerciseUtils.exerciseAnglesMap[exercise]!!
        return getPose(normalizedLandmarks)
    }

    private fun normalize(landmarks: MutableList<PointF3D>): MutableList<PointF3D> {
        val normalizedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
        // Normalize translation.
        val center: PointF3D = average(
            landmarks[PoseLandmark.LEFT_HIP], landmarks[PoseLandmark.RIGHT_HIP]
        )
        subtractAll(center, normalizedLandmarks)

        // Normalize scale.
        multiplyAll(
            normalizedLandmarks,
            1 / getPoseSize(normalizedLandmarks)
        )
        // Multiplication by 100 is not required, but makes it easier to debug.
        multiplyAll(normalizedLandmarks, 100f)
        return normalizedLandmarks
    }

    // IMPORTANT!! This needs to be called once
    // Translation normalization should've been done prior to calling this method.
    private fun getPoseSize(landmarks: MutableList<PointF3D>): Float {
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
        return maxDistance
    }


}