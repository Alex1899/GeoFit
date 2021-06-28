package com.example.geofitapp.posedetection.poseDetector

import android.content.Context
import android.util.Log
import com.example.geofitapp.posedetection.VisionProcessorBase
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.posedetection.poseDetector.jointAngles.FramePose
import com.example.geofitapp.posedetection.poseDetector.repAnalysis.ExerciseAnalysis
import com.example.geofitapp.posedetection.poseDetector.repCounter.BicepCurlRepCounter
import com.example.geofitapp.posedetection.poseDetector.repCounter.ExerciseRepCounter
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import java.util.ArrayList
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
    private val exercise: String
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithRepCounting>(context) {

    private val detector: PoseDetector = PoseDetection.getClient(options)
    private val classificationExecutor: Executor
    private var repCounter: ExerciseRepCounter? = null
    private var repAnalyzer: ExerciseAnalysis? = null
    private var exerciseJointAngles = mutableListOf<Double>()
    private var lastRepResult = 0
    private var feedback = ""

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
                        repCounter = ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise]!!.first
                        repAnalyzer = ExerciseUtils.exerciseRepCounterAnalyzerMap[exercise]!!.second
                    }


                    PoseWithRepCounting(pose)
                }
            )
    }

    override fun onSuccess(
        results: PoseWithRepCounting,
        graphicOverlay: GraphicOverlay
    ) {
        if (results.pose.allPoseLandmarks.isEmpty()) {
            return
        }
        val jointAngles =
            FramePose(exercise).getFramePose(ExerciseUtils.convertToPoint3D(results.pose.allPoseLandmarks))

        exerciseJointAngles.addAll(jointAngles.values)

        val repCounterResult = ExerciseUtils.countReps(repCounter!!, jointAngles)
        if(repCounterResult !== null){
            if (repCounterResult > lastRepResult){
                lastRepResult = repCounterResult
                //analyze rep
                feedback = repAnalyzer!!.analyseRep(exerciseJointAngles)
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
                feedback
            )
        )
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    companion object {
        private val TAG = "PoseDetectorProcessor"
    }
}
