package com.example.geofitapp.posedetection.poseDetector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.example.geofitapp.databinding.ActivityCameraXlivePreviewBinding
import com.example.geofitapp.posedetection.VisionProcessorBase

import com.example.geofitapp.posedetection.helperClasses.FrameMetadata
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.FramePose
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.google.android.gms.tasks.Task

import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.*

import java.nio.ByteBuffer

import java.util.concurrent.Executor
import java.util.concurrent.Executors


/** A processor to run pose detector.  */
class PoseDetectorProcessor(
    context: Context,
    options: PoseDetectorOptionsBase,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val exercise: MutableList<String>
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithRepCounting>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor
    private var repCounter: ExerciseRepCounter? = null
    private var repAnalyzer: ExerciseAnalysis? = null
    private lateinit var exerciseProcessor: ExerciseProcessor
    private var exerciseJointAngles = mutableListOf<Double>()
    private var avgAngle: Float? = null
    private var leftJointAngles = mutableListOf<Double>()
    private var rightJointAngles = mutableListOf<Double>()
    private var repAnglesList: MutableList<Double>? = null


    private var exerciseStarted: Boolean? = null


    companion object {
        private val TAG = "PoseDetectorProcessor"
        var torsoLengths = mutableListOf<Float>()
    }

//    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /**
     * Internal class to hold Pose and classification results.
     */
    class PoseWithRepCounting(
        val pose: Pose,
    )

    init {
        classificationExecutor = Executors.newSingleThreadExecutor()

    }

    override fun stop() {
        super.stop()
        detector.close()
    }


    override
    fun detectInImage(image: InputImage): Task<PoseWithRepCounting> {
        TODO()

    }

    override
    fun detectInImage(image: MlImage): Task<PoseWithRepCounting> {
        return detector
            .process(image)
            .continueWith(
                classificationExecutor,
                { task ->
                    val pose = task.result
                    Log.i("PoseDetectorProcessor", "Rep counter initialized")
                    if (repCounter == null && repAnalyzer == null) {
                        repCounter =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.first
                        repAnalyzer =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.second
                        exerciseProcessor =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.third
                    }

                    exerciseProcessor.initilizePose(exercise[0], pose, repCounter!!, repAnalyzer!!)

                    PoseWithRepCounting(pose)
                }
            )
    }


    @SuppressLint("SetTextI18n")
    private fun resetInfo(binding: ActivityCameraXlivePreviewBinding) {
        repCounter?.resetTotalReps()
        exerciseProcessor.resetDetails()
        exerciseStarted = false
        binding.detailsOverlayView.visibility = View.GONE
//        binding.repsOverlayText.text = "0"
//        binding.errorsOverlayText.text = "0"
//        binding.setsOverlayText.text = "1"
//        binding.paceOverlayText.text = "0.0s"
//        binding.sideOverlayText.text = "N/A"
    }

    override fun onSuccess(
        results: PoseWithRepCounting,
        graphicOverlay: GraphicOverlay,
        binding: ActivityCameraXlivePreviewBinding
    ) {
        if (results.pose.allPoseLandmarks.isEmpty()) {
            Log.i("RepCount", "\n=================NOT STARTED=================")
            resetInfo(binding)
            return
        }
        if (exerciseStarted == null || exerciseStarted == false) {
            exerciseStarted = true
        }

        if(binding.detailsOverlayView.visibility == View.GONE){
            binding.detailsOverlayView.visibility = View.VISIBLE
        }

        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                results.pose,
                exercise[0],
                exerciseProcessor.side,
                exerciseProcessor.lastRepResult.toString(),
                exerciseProcessor.jointAnglesMap,
                exerciseProcessor.feedBack,
                binding,
                exerciseProcessor.pace,
                visualizeZ,
                rescaleZForVisualization

            )
        )
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    override fun processBitmap(
        bitmap: Bitmap?,
        graphicOverlay: GraphicOverlay,
        detailsOverlay: DetailsOverlay
    ) {
        TODO("Not yet implemented")
    }

    override fun processByteBuffer(
        data: ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay,
        detailsOverlay: DetailsOverlay
    ) {
        TODO("Not yet implemented")
    }

}
