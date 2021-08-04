package com.example.geofitapp.posedetection.poseDetector

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.example.geofitapp.databinding.ActivityCameraXlivePreviewBinding
import com.example.geofitapp.posedetection.VisionProcessorBase

import com.example.geofitapp.posedetection.helperClasses.FrameMetadata
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.FramePose
import com.example.geofitapp.posedetection.poseDetector.jointAngles.Utils
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.example.geofitapp.ui.exerciseSetDetails.ExerciseSetDetails
import com.example.geofitapp.ui.exerciseSetDetails.ExerciseSetDetailsActivity
import com.google.android.gms.tasks.Task

import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.*

import java.nio.ByteBuffer

import java.util.concurrent.Executor
import java.util.concurrent.Executors


/** A processor to run pose detector.  */
class PoseDetectorProcessor(
    private val context: Context,
    options: PoseDetectorOptionsBase,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val exercise: MutableList<String>,
    private val totalReps: Int
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithRepCounting>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor
    private var repCounter: ExerciseRepCounter? = null
    private var repAnalyzer: ExerciseAnalysis? = null
    private var cacheSide: String? = null
    private var exerciseProcessor: ExerciseProcessor? = null


    companion object {
        private val TAG = "PoseDetectorProcessor"
        var torsoLengths = mutableListOf<Float>()
        var exerciseFinished = false
    }

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
                        repCounter!!.overallTotalReps = totalReps

                        repAnalyzer =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.second
                    }

                    PoseWithRepCounting(pose)
                }
            )
    }


    @SuppressLint("SetTextI18n")
    override fun resetInfo(binding: ActivityCameraXlivePreviewBinding) {
        repCounter!!.resetTotalReps()
        binding.detailsOverlayView.visibility = View.GONE
        binding.repsOverlayText.text = "0"
        binding.errorsOverlayText.text = "0"
        binding.setsOverlayText.text = "1"
        binding.paceOverlayText.text = "0.0s"
        binding.sideOverlayText.text = "N/A"
    }

    private fun startSetDetailsActivity(
        exerciseProcessor: ExerciseProcessor,
        binding: ActivityCameraXlivePreviewBinding
    ) {
        val intent = Intent(context, ExerciseSetDetailsActivity::class.java)
        // sets, reps, time taken, and rest timer
        val allAngles = mutableListOf<Triple<String, MutableList<Double>, Triple<Float, Float, Boolean>>>()
        for(triple in exerciseProcessor.allAnglesOfInterest.values.toList()){
            allAngles.add(triple)
        }


        val reps = "${binding.repsOverlayText.text}${binding.testRep.text}"
        val sets = "${binding.setsOverlayText.text}${binding.testSet.text}"

        val details = ExerciseSetDetails(
            sets,
            reps,
            String.format("%.1f", exerciseProcessor.pace)+ "s",
            String.format("%.1f", exerciseProcessor.exerciseFinishTime) + "s",
            allAngles,
        )
        intent.putExtra("exerciseSetDetails", details)
        startActivity(context, intent, null)
    }

    override fun onSuccess(
        results: PoseWithRepCounting,
        graphicOverlay: GraphicOverlay,
        binding: ActivityCameraXlivePreviewBinding
    ) {

        if (exerciseFinished && exerciseProcessor !== null) {
            //navigate
            startSetDetailsActivity(exerciseProcessor!!, binding)
            exerciseFinished = false
        }
        if (results.pose.allPoseLandmarks.isEmpty()) {
            Log.i("RepCount", "\n=================NOT STARTED=================")
            resetInfo(binding)
            return
        }

        if (binding.detailsOverlayView.visibility == View.GONE) {
            binding.detailsOverlayView.visibility = View.VISIBLE
        }

        if (cacheSide == null) {
            cacheSide = ExerciseUtils.detectSide(results.pose)
        }

        val jointAnglesMap =
            FramePose(
                exercise[0],
                cacheSide!!
            ).getFramePose(ExerciseUtils.convertToPoint3D(results.pose.allPoseLandmarks))

        exerciseProcessor = ExerciseUtils.countReps(
            repCounter!!,
            jointAnglesMap,
            cacheSide!!
        )
        exerciseProcessor!!.side = cacheSide!!
        exerciseProcessor!!.jointAnglesMap = jointAnglesMap

        var feedBack = ""
        if (exerciseProcessor!!.repFinished!!) {
            exerciseProcessor!!.getFeedback(repAnalyzer!!)
            exerciseProcessor!!.repFinished = false
            feedBack = exerciseProcessor!!.getRepFormResult()
            Log.i("ProcessorKKK", "feedback = $feedBack")
        }

        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                results.pose,
                exercise[0],
                exerciseProcessor!!.side,
                exerciseProcessor!!.lastRepResult.toString(),
                exerciseProcessor!!.jointAnglesMap,
                feedBack,
                binding,
                exerciseProcessor!!.pace,
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
