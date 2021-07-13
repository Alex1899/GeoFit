package com.example.geofitapp.posedetection.poseDetector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.geofitapp.posedetection.VisionProcessorBase
import com.example.geofitapp.posedetection.helperClasses.FrameMetadata
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.FramePose
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.*
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/** A processor to run pose detector.  */
class PoseDetectorProcessor(
    private val context: Context,
    options: PoseDetectorOptionsBase,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val runClassification: Boolean,
    private val isStreamMode: Boolean,
    private val exercise: MutableList<String>
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithRepCounting>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor
    private var repCounter: ExerciseRepCounter? = null
    private var repAnalyzer: ExerciseAnalysis? = null
    private var exerciseJointAngles = mutableListOf<Double>()
    private var leftJointAngles = mutableListOf<Double>()
    private var rightJointAngles = mutableListOf<Double>()

    private var lastRepResult = 0
    private var feedback = ""
    private var torso: Float? = null
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
        return detector
            .process(image)
            .continueWith(
                classificationExecutor,
                { task ->
                    val pose = task.result
                    // need a way to skip empty pose landmarks...
                    if (repCounter == null && repAnalyzer == null) {
                        Log.i("PoseDetectorProcessor", "Rep counter initialized")
                        repCounter =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.first
                        repAnalyzer =
                            ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise[0]]!!.second
                    }


                    PoseWithRepCounting(pose)
                }
            )
    }
    private fun resetInfo() {
        repCounter!!.resetTotalReps()
        exerciseJointAngles.clear()
        leftJointAngles.clear()
        rightJointAngles.clear()
        exerciseJointAngles.clear()
        lastRepResult = 0
        feedback = ""
        torsoLengths.clear()
        torso = null
        exerciseStarted = false
    }

    override fun onSuccess(
        results: PoseWithRepCounting,
        graphicOverlay: GraphicOverlay,
        detailsOverlay: DetailsOverlay
    ) {
        if (results.pose.allPoseLandmarks.isEmpty()) {
            Log.i("RepCount", "\n=================NOT STARTED=================")
            resetInfo()
            return
        }
        if(exerciseStarted == null || exerciseStarted == false){
            exerciseStarted = true
        }

        val side = ExerciseUtils.detectSide(results.pose)
        if (exercise.size > 1) {
            exercise.removeAt(1)
        }
        exercise.add(side)

        Log.i("Side", "side detected $side")
        feedback = ""

        if (torso == null) {
            if (torsoLengths.size == 20) {
                torso = (torsoLengths.sum()) / 20
                Log.i("Torso", "getting average = $torso")
            }
        }

        val jointAngles =
            FramePose(exercise[0], side, torso).getFramePose(
                ExerciseUtils.convertToPoint3D(results.pose.allPoseLandmarks),
            )

        // this part needs to be different for each exercise
        var repCounterResult: Int?
        if (side == "front") {
            return
//            rightJointAngles.add(jointAngles[PoseLandmark.RIGHT_ELBOW]!!)
//            leftJointAngles.add(jointAngles[PoseLandmark.LEFT_ELBOW]!!)
//            repCounterResult = ExerciseUtils.countReps(repCounter!!, jointAngles, side)
        } else {
            // add angles in single frame
            exerciseJointAngles.addAll(jointAngles.values)
            repCounterResult = ExerciseUtils.countReps(repCounter!!, jointAngles, side)

        }

        // maybe pass a list of angles to countReps instead of map of one angle to landmark
        if (repCounterResult !== null) {
            if (repCounterResult > lastRepResult) {
                lastRepResult = repCounterResult
                // analyze rep
                if (side == "front") {
                    feedback = repAnalyzer!!.analyseRepFront(leftJointAngles, rightJointAngles)

                    leftJointAngles.clear()
                    rightJointAngles.clear()
                } else {
                    feedback = repAnalyzer!!.analyseRep(exerciseJointAngles)
                    exerciseJointAngles.clear()
                }
            }
        }

        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                results.pose,
                showInFrameLikelihood,
                visualizeZ,
                rescaleZForVisualization,
                exercise,
                lastRepResult.toString(),
                jointAngles,
                feedback,
                detailsOverlay
            )
        )
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    override fun processBitmap(bitmap: Bitmap?, graphicOverlay: GraphicOverlay?) {
        TODO("Not yet implemented")
    }

    override fun processByteBuffer(
        data: ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay?
    ) {
        TODO("Not yet implemented")
    }
}
