package com.example.geofitapp.posedetection.posedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.example.geofitapp.posedetection.helperclasses.GraphicOverlay
import com.example.geofitapp.posedetection.posedetector.repanalysis.ExerciseUtils
import com.example.geofitapp.posedetection.posedetector.repanalysis.FramePose
import com.google.common.primitives.Ints
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*


/** Draw the detected pose in preview.  */
class PoseGraphic internal constructor(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val exercise: String
): GraphicOverlay.Graphic(overlay) {
    private var zMin = java.lang.Float.MAX_VALUE
    private var zMax = java.lang.Float.MIN_VALUE
    private val leftPaint: Paint
    private val rightPaint: Paint
    private val whitePaint: Paint = Paint()

    init {
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        leftPaint = Paint()
        leftPaint.strokeWidth = STROKE_WIDTH
        leftPaint.color = Color.GREEN
        rightPaint = Paint()
        rightPaint.strokeWidth = STROKE_WIDTH
        rightPaint.color = Color.YELLOW
    }

    override fun draw(canvas: Canvas) {

        val landmarks = pose.allPoseLandmarks

        // Clean landmarks
        if (landmarks.isEmpty()) {
            return
        }

        // get exercise pose landmarks
        // Draw all the points from shoulders to ankles
        for (i in 11..28) {
            if (i in 17..22) continue //skip finger drawing

            drawPoint(canvas, landmarks[i].position3D, whitePaint)
            if (visualizeZ && rescaleZForVisualization) {
                zMin = zMin.coerceAtMost(landmarks[i].position3D.z)
                zMax = zMax.coerceAtLeast(landmarks[i].position3D.z)
            }
        }
        val jointAngles = FramePose(exercise).getFramePose(ExerciseUtils.convertToPoint3D(landmarks))

        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)


        drawLine(canvas, leftShoulder, rightShoulder, whitePaint)
        drawLine(canvas, leftHip, rightHip, whitePaint)
        // Left body
        drawLine(canvas, leftShoulder, leftElbow, leftPaint)
        drawLine(canvas, leftElbow, leftWrist, leftPaint)
        drawLine(canvas, leftShoulder, leftHip, leftPaint)
        drawLine(canvas, leftHip, leftKnee, leftPaint)
        drawLine(canvas, leftKnee, leftAnkle, leftPaint)

        // Right body
        drawLine(canvas, rightShoulder, rightElbow, rightPaint)
        drawLine(canvas, rightElbow, rightWrist, rightPaint)
        drawLine(canvas, rightShoulder, rightHip, rightPaint)
        drawLine(canvas, rightHip, rightKnee, rightPaint)
        drawLine(canvas, rightKnee, rightAnkle, rightPaint)

//        val test = listOf(Pair(rightElbow!!.position3D, ExerciseUtils.getAngle(rightShoulder!!.position3D, rightElbow.position3D, rightWrist!!.position3D)),
//            Pair(leftElbow!!.position3D, ExerciseUtils.getAngle(leftShoulder!!.position3D, leftElbow.position3D, leftWrist!!.position3D)))

        // Draw inFrameLikelihood for all points
        if (showInFrameLikelihood) {
            Log.i("PoseGraphic", "drawing angle values")

            for (pair in jointAngles) {
                canvas.drawText(
                    String.format(Locale.US, "%.2f", pair.second),
                    translateX(pair.first.x),
                    translateY(pair.first.y),
                    whitePaint
                )
            }
        }

    }

    private fun drawPoint(canvas: Canvas, landmark: PointF3D, paint: Paint) {
        canvas.drawCircle(translateX(landmark.x), translateY(landmark.y), DOT_RADIUS, paint)
    }

    private fun drawLine(
        canvas: Canvas,
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?,
        paint: Paint
    ) {
        if (startLandmark == null || endLandmark == null) return
        // When visualizeZ is true, sets up the paint to draw body line in different colors based on
        // their z values.
        if (visualizeZ) {
            val start = startLandmark.position3D
            val end = endLandmark.position3D

            // Gets the range of z value.
            val zLowerBoundInScreenPixel: Float
            val zUpperBoundInScreenPixel: Float

            if (rescaleZForVisualization) {
                zLowerBoundInScreenPixel = Math.min(-0.001f, scale(zMin))
                zUpperBoundInScreenPixel = Math.max(0.001f, scale(zMax))
            } else {
                // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
                val defaultRangeFactor = 1f
                zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
                zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
            }

            // Gets average z for the current body line
            val avgZInImagePixel = (start.z + end.z) / 2
            val zInScreenPixel = scale(avgZInImagePixel)

            if (zInScreenPixel < 0) {
                // Sets up the paint to draw the body line in red if it is in front of the z origin.
                // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
                // color. The larger the value is, the more red it will be.
                var v = (zInScreenPixel / zLowerBoundInScreenPixel * 255).toInt()
                v = Ints.constrainToRange(v, 0, 255)
                paint.setARGB(255, 255, 255 - v, 255 - v)
            } else {
                // Sets up the paint to draw the body line in blue if it is behind the z origin.
                // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
                // color. The larger the value is, the more blue it will be.
                var v = (zInScreenPixel / zUpperBoundInScreenPixel * 255).toInt()
                v = Ints.constrainToRange(v, 0, 255)
                paint.setARGB(255, 255 - v, 255 - v, 255)
            }

            canvas.drawLine(
                translateX(start.x),
                translateY(start.y),
                translateX(end.x),
                translateY(end.y),
                paint
            )
        } else {
            val start = startLandmark.position
            val end = endLandmark.position
            canvas.drawLine(
                translateX(start.x),
                translateY(start.y),
                translateX(end.x),
                translateY(end.y),
                paint
            )
        }
    }
    companion object {
        private val DOT_RADIUS = 8.0f
        private val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private val STROKE_WIDTH = 10.0f
    }
}