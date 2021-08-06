package com.example.geofitapp.posedetection.poseDetector

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityCameraXlivePreviewBinding
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.google.common.primitives.Ints
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/** Draw the detected pose in preview.  */
class PoseGraphic internal constructor(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val exercise: String,
    private val side: String,
    private val repCounterResult: String,
    private val jointAnglesMap: MutableMap<Int, Double>,
    private val feedback: String,
    private val binding: ActivityCameraXlivePreviewBinding,
    private val pace: Float,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean

    ) : GraphicOverlay.Graphic(overlay) {
    private var zMin = java.lang.Float.MAX_VALUE
    private var zMax = java.lang.Float.MIN_VALUE
    private val leftPaint: Paint
    private val rightPaint: Paint
    private val whitePaint: Paint = Paint()
    private val repResultPaint: Paint

    private val DOT_RADIUS = 10.0f
    private val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
    private val STROKE_WIDTH = 10.0f
    private val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f


    init {
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        leftPaint = Paint()
        leftPaint.strokeWidth = STROKE_WIDTH
        leftPaint.color = Color.YELLOW
        rightPaint = Paint()
        rightPaint.strokeWidth = STROKE_WIDTH
        rightPaint.color = Color.BLUE

        repResultPaint = Paint()
        repResultPaint.color = Color.WHITE
        repResultPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
        repResultPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)

    }

    @SuppressLint("SetTextI18n")
    override fun draw(canvas: Canvas) {

        val landmarks = pose.allPoseLandmarks

        // Clean landmarks
        if (landmarks.isEmpty()) {
            return
        }

        binding.repsOverlayText.text = repCounterResult
        binding.paceOverlayText.text = String.format("%.1f", pace) + "s"
        binding.sideOverlayText.text = side

        if(feedback === "Wrong"){
            val prev = binding.errorsOverlayText.text.toString()
            binding.errorsOverlayText.text = (prev.toInt() + 1).toString()
        }
//        detailsOverlay.addDetails(repCounterResult, String.format("%.1f", pace), side)


//        // Draw all the points
//        for (landmark in landmarks) {
//            drawPoint(canvas, landmark.position3D, whitePaint)
//            if (visualizeZ && rescaleZForVisualization) {
//                zMin = min(zMin, landmark.position3D.z)
//                zMax = max(zMax, landmark.position3D.z)
//            }
//        }
//        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
//        val lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
//        val lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
//        val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
//        val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
//        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
//        val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
//        val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
//        val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
//        val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
//        val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)
//
//        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
//        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
//        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
//        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
//        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
//        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
//        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
//        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
//        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
//        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
//        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
//        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
//
//        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
//        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
//        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
//        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
//        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
//        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
//        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
//        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
//        val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
//        val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
//
//        // Face
//        drawLine(canvas, nose, lefyEyeInner, whitePaint)
//        drawLine(canvas, lefyEyeInner, lefyEye, whitePaint)
//        drawLine(canvas, lefyEye, leftEyeOuter, whitePaint)
//        drawLine(canvas, leftEyeOuter, leftEar, whitePaint)
//        drawLine(canvas, nose, rightEyeInner, whitePaint)
//        drawLine(canvas, rightEyeInner, rightEye, whitePaint)
//        drawLine(canvas, rightEye, rightEyeOuter, whitePaint)
//        drawLine(canvas, rightEyeOuter, rightEar, whitePaint)
//        drawLine(canvas, leftMouth, rightMouth, whitePaint)
//
//        drawLine(canvas, leftShoulder, rightShoulder, whitePaint)
//        drawLine(canvas, leftHip, rightHip, whitePaint)
//
//        // Left body
//        drawLine(canvas, leftShoulder, leftElbow, leftPaint)
//        drawLine(canvas, leftElbow, leftWrist, leftPaint)
//        drawLine(canvas, leftShoulder, leftHip, leftPaint)
//        drawLine(canvas, leftHip, leftKnee, leftPaint)
//        drawLine(canvas, leftKnee, leftAnkle, leftPaint)
//        drawLine(canvas, leftWrist, leftThumb, leftPaint)
//        drawLine(canvas, leftWrist, leftPinky, leftPaint)
//        drawLine(canvas, leftWrist, leftIndex, leftPaint)
//        drawLine(canvas, leftIndex, leftPinky, leftPaint)
//        drawLine(canvas, leftAnkle, leftHeel, leftPaint)
//        drawLine(canvas, leftHeel, leftFootIndex, leftPaint)
//
//        // Right body
//        drawLine(canvas, rightShoulder, rightElbow, rightPaint)
//        drawLine(canvas, rightElbow, rightWrist, rightPaint)
//        drawLine(canvas, rightShoulder, rightHip, rightPaint)
//        drawLine(canvas, rightHip, rightKnee, rightPaint)
//        drawLine(canvas, rightKnee, rightAnkle, rightPaint)
//        drawLine(canvas, rightWrist, rightThumb, rightPaint)
//        drawLine(canvas, rightWrist, rightPinky, rightPaint)
//        drawLine(canvas, rightWrist, rightIndex, rightPaint)
//        drawLine(canvas, rightIndex, rightPinky, rightPaint)
//        drawLine(canvas, rightAnkle, rightHeel, rightPaint)
//        drawLine(canvas, rightHeel, rightFootIndex, rightPaint)

//        var elbow: PoseLandmark? = null
//        if (exercise[1] == "front") {
//            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
//            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!
//
//            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!
//            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!
//
//            drawPoint(canvas, rightElbow.position3D, whitePaint)
//            drawPoint(canvas, leftElbow.position3D, whitePaint)
//
//            drawPoint(canvas, rightShoulder.position3D, whitePaint)
//            drawPoint(canvas, leftShoulder.position3D, whitePaint)
//
//        } else {
//            elbow= if (exercise[1] == "right") {
//                pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
//            } else {
//                pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!
//            }
//            drawPoint(canvas, elbow.position3D, whitePaint)
//        }

        // get landmarks
        val landmarksList = mutableListOf<PoseLandmark>()
        for ((lmId, _) in jointAnglesMap) {
            val lm = pose.getPoseLandmark(lmId)!!
            landmarksList.add(lm)
        }
        val p = if(side == "right") rightPaint else leftPaint

        for(index in landmarksList.indices) {
            if(index + 1 >= landmarksList.size){
                drawPoint(canvas, landmarksList[index].position3D, p)
                return
            }
            drawPoint(canvas, landmarksList[index].position3D, p)
            drawLine(canvas, landmarksList[index], landmarksList[index+1], whitePaint)
        }

        // Draw degrees for all points
        for ((lmId, angle) in jointAnglesMap) {
            val lm = pose.getPoseLandmark(lmId)!!
            canvas.drawText(
//                    String.format("%.1f", angle),
                angle.roundToInt().toString(),
                translateX(lm.position.x),
                translateY(lm.position.y),
                whitePaint
            )
        }

    }

    private fun drawPoint(canvas: Canvas, landmark: PointF3D, paint: Paint) {
        //maybeUpdatePaintColor(paint, canvas, landmark.z)
        canvas.drawCircle(translateX(landmark.x), translateY(landmark.y), DOT_RADIUS, paint)
    }

    internal fun drawLine(
        canvas: Canvas,
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?,
        paint: Paint
    ) {
        val start = startLandmark!!.position3D
        val end = endLandmark!!.position3D

        // Gets average z for the current body line
        val avgZInImagePixel = (start.z + end.z) / 2
        //maybeUpdatePaintColor(paint, canvas, avgZInImagePixel)

        canvas.drawLine(
            translateX(start.x),
            translateY(start.y),
            translateX(end.x),
            translateY(end.y),
            paint
        )
    }

    private fun maybeUpdatePaintColor(
        paint: Paint,
        canvas: Canvas,
        zInImagePixel: Float
    ) {
        if (!visualizeZ) {
            return
        }

        // When visualizeZ is true, sets up the paint to different colors based on z values.
        // Gets the range of z value.
        val zLowerBoundInScreenPixel: Float
        val zUpperBoundInScreenPixel: Float

        if (rescaleZForVisualization) {
            zLowerBoundInScreenPixel = min(-0.001f, scale(zMin))
            zUpperBoundInScreenPixel = max(0.001f, scale(zMax))
        } else {
            // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
            val defaultRangeFactor = 1f
            zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
            zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
        }

        val zInScreenPixel = scale(zInImagePixel)

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
    }

}