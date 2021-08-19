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
    private val side: String,
    private val repCounterResult: String,
    private val jointAnglesMap: MutableMap<Int, Double>,
    private val binding: ActivityCameraXlivePreviewBinding,
    private val pace: Float,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val totalErrors: Int

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
        binding.errorsOverlayText.text = totalErrors.toString()


        // get landmarks
        val landmarksList = mutableListOf<PoseLandmark>()
        for ((lmId, _) in jointAnglesMap) {
            val lm = pose.getPoseLandmark(lmId)!!
            landmarksList.add(lm)
        }
        val p = if (side == "right") rightPaint else leftPaint

        if (side != "front") {
            for (index in landmarksList.indices) {
                if (index + 1 >= landmarksList.size) {
                    drawPoint(canvas, landmarksList[index].position3D, p)
                    return
                }
                drawPoint(canvas, landmarksList[index].position3D, p)
                drawLine(canvas, landmarksList[index], landmarksList[index + 1], whitePaint)
            }
        } else {
            // shoulder press specific
            for (lm in landmarksList) {
                drawPoint(canvas, lm.position3D, p)
            }
            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.LEFT_WRIST),
                pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                whitePaint
            )
            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                whitePaint
            )

            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                whitePaint
            )
            drawPoint(canvas, pose.getPoseLandmark(PoseLandmark.LEFT_HIP)!!.position3D, p)

            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST),
                pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                whitePaint
            )
            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                whitePaint
            )
            drawLine(
                canvas,
                pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                pose.getPoseLandmark(PoseLandmark.RIGHT_HIP),
                whitePaint
            )
            drawPoint(canvas, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)!!.position3D, p)


        }

        // Draw degrees for all points
//        for ((lmId, angle) in jointAnglesMap) {
//            val lm = pose.getPoseLandmark(lmId)!!
//            canvas.drawText(
////                    String.format("%.1f", angle),
//                angle.roundToInt().toString(),
//                translateX(lm.position.x),
//                translateY(lm.position.y),
//                whitePaint
//            )
//        }

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