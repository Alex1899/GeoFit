package com.example.geofitapp.posedetection.poseDetector

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat.startActivity
import com.example.geofitapp.databinding.ActivityCameraXlivePreviewBinding
import com.example.geofitapp.posedetection.VisionProcessorBase

import com.example.geofitapp.posedetection.helperClasses.FrameMetadata
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.JointAngles
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.example.geofitapp.ui.cameraPreview.CameraXLivePreviewActivity
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.example.geofitapp.ui.exerciseSetDetails.ExerciseSetDetails
import com.example.geofitapp.ui.exerciseSetDetails.ExerciseSetDetailsActivity
import com.example.geofitapp.ui.exerciseSetDetails.restTimer.PrefUtil
import com.example.geofitapp.ui.exerciseSetDetails.restTimer.RestTimer
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
    private val totalReps: Int,
    private val cameraProvider: ProcessCameraProvider,
    private val textToSpeech: TextToSpeech,
    private val activity: CameraXLivePreviewActivity
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithRepCounting>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor
    private var repCounter: ExerciseRepCounter? = null
    private var repAnalyzer: ExerciseAnalysis? = null
    private var cacheSide: String? = null
    private var feedbackMemo = mutableListOf<String>()
    private var intraSetFeedbackInt = 5
    private var intraSetIntWindow = 0
    private var totalErrors = 0
    private var intraSetErrCount = 0


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

                    if (pose.allPoseLandmarks.isNotEmpty()) {
                        if (cacheSide == null) {
                            cacheSide = ExerciseUtils.getExerciseSide(exercise[0], pose)
                        }

                        if (repCounter == null) {
                            Log.i("BugHunt", "processor initialized repCounter")
                            repCounter =
                                ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.first
                            repCounter!!.overallTotalReps = totalReps
                            repCounter!!.init(cacheSide!!)

                            repAnalyzer =
                                ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.second

                            ExerciseProcessor.setAnglesOfInterestMap(ExerciseUtils.exerciseAnglesOfInterestMap[exercise[0]]!!)
                        }
                    }

                    PoseWithRepCounting(pose)
                }
            )
    }

    @SuppressLint("SetTextI18n")
    override fun resetInfo(binding: ActivityCameraXlivePreviewBinding) {
        repCounter?.resetTotalReps()
        repCounter = null
        ExerciseProcessor.resetDetails()
        Log.i("BugHunt", "processor reset repCounter")
        totalErrors = 0
        intraSetIntWindow = 0
        binding.detailsOverlayView.visibility = View.GONE
        binding.repsOverlayText.text = "0"
        binding.errorsOverlayText.text = "0"
        binding.setsOverlayText.text = "1"
        binding.paceOverlayText.text = "0.0s"
        binding.sideOverlayText.text = "N/A"
    }

    private fun startSetDetailsActivity(
        binding: ActivityCameraXlivePreviewBinding
    ) {
        val intent = Intent(context, ExerciseSetDetailsActivity::class.java)
        // sets, reps, time taken, and rest timer
        val allAngles =
            mutableListOf<Triple<Pair<String, String?>, Pair<MutableList<Double>, MutableList<Double>?>, List<Triple<Float?, Float?, Boolean>>>>()
        val ls = ExerciseProcessor.allAnglesOfInterest.values.toList()
        for (triple in ls) {
            allAngles.add(triple)
        }

        val reps = "${binding.repsOverlayText.text}${binding.testRep.text}"
        val set = binding.testSet.text.toString().split("/")[1].toInt()

        val details = ExerciseSetDetails(
            exercise[0],
            set,
            binding.setsOverlayText.text.toString().toInt(),
            reps,
            String.format("%.1f", ExerciseProcessor.pace) + "s",
            String.format("%.1f", ExerciseProcessor.exerciseFinishTime) + "s",
            allAngles,
            ExerciseProcessor.feedBack
        )
        Log.i("testXy", "allangles = $allAngles")
        Log.i("testXy", "feedback = ${ExerciseProcessor.feedBack}")

        intent.putExtra("exerciseSetDetails", details)

        Log.i("BugHunt", "ExerciseProcessor before sending: ${ExerciseProcessor.allAnglesOfInterest}")
        Log.i("BugHunt", "allAngles before sending: $allAngles")

        PrefUtil.setTimerState(RestTimer.TimerState.NotStarted, context)
        startActivity(context, intent, null)
//        resetInfo(binding)

        activity.finish()
    }

    override fun onSuccess(
        results: PoseWithRepCounting,
        graphicOverlay: GraphicOverlay,
        binding: ActivityCameraXlivePreviewBinding
    ) {

        if (exerciseFinished) {
            //navigate
            cameraProvider.unbindAll()
            startSetDetailsActivity(binding)
            exerciseFinished = false
            Log.i("BugHunt", "CAMERA CLOSED, PoseProcessor returned")
            return
        }
        if (results.pose.allPoseLandmarks.isEmpty()) {
//            resetInfo(binding)
            return
        }

        if (binding.detailsOverlayView.visibility == View.GONE) {
            binding.detailsOverlayView.visibility = View.VISIBLE
        }

        val jointAnglesMap =
            JointAngles(
                exercise[0],
                cacheSide!!
            ).getFramePose(ExerciseUtils.convertToPoint3D(results.pose.allPoseLandmarks))

        ExerciseUtils.countReps(
            repCounter!!,
            jointAnglesMap,
            ExerciseUtils.mainAOIindexMap[exercise[0]]!![cacheSide]!!
        )
        ExerciseProcessor.side = cacheSide!!
        ExerciseProcessor.jointAnglesMap = jointAnglesMap

        var feedBack = ""
        if (ExerciseProcessor.repFinished!!) {
            ExerciseProcessor.getFeedback(repAnalyzer!!)
            ExerciseProcessor.repFinished = false
            val pair = ExerciseProcessor.getRepFormResult()
            feedBack = pair.first
            if (pair.second.isNotEmpty()) {
                //&& pair.second!!.length <= TextToSpeech.getMaxSpeechInputLength()
//                Log.i("TTSFeedback", "feedback list =${pair.second}")
                feedbackMemo.addAll(pair.second)
            }

            if (feedBack == "Wrong") {
                totalErrors += 1
                intraSetErrCount += 1
            }
            // give feedback after 60% of reps of first 5 are wrong
            if (intraSetErrCount == (5 * 0.6).toInt() ||
                ExerciseProcessor.lastRepResult == intraSetFeedbackInt
            ) {
                intraSetFeedbackInt = ExerciseProcessor.lastRepResult + 5
                intraSetErrCount = 0
                textToSpeech.speak(
                    getSpeechFeedback(feedbackMemo.distinct().toMutableList()),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "tts1"
                );
                feedbackMemo.clear()
            }

        }

        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                results.pose,
                ExerciseProcessor.side,
                ExerciseProcessor.lastRepResult.toString(),
                ExerciseProcessor.jointAnglesMap,
                binding,
                ExerciseProcessor.pace,
                visualizeZ,
                rescaleZForVisualization,
                totalErrors
            )
        )
    }

    private fun getSpeechFeedback(stringsArr: MutableList<String>): String {
        if (stringsArr.size > 3) {
            val arr = stringsArr.slice(0 until 3).toMutableList()
            return "${arr[0]}, ${arr[1]}, and ${arr[2]}"
        }

        if (stringsArr.size == 3) {
            return "${stringsArr[0]}, ${stringsArr[1]}, and ${stringsArr[2]}"
        }

        if (stringsArr.size == 2) {
            return "${stringsArr[0]} and ${stringsArr[1]}"
        }

        return if (stringsArr.isEmpty()) "Well done! Keep going" else stringsArr[0]

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
