package com.example.geofitapp.ui.cameraPreview

//import com.example.geofitapp.posedetection.poseDetector.Classifier

import android.R.string
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityCameraXlivePreviewBinding
import com.example.geofitapp.posedetection.helperClasses.GraphicOverlay
import com.example.geofitapp.posedetection.helperClasses.VisionImageProcessor
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor
import com.example.geofitapp.posedetection.preference.PreferenceUtils
import com.example.geofitapp.ui.cameraPreview.detailsOverlay.DetailsOverlay
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.common.MlKitException
import java.util.*


/** Live preview demo app for ML Kit APIs using CameraX.  */
@KeepName
class CameraXLivePreviewActivity : AppCompatActivity(),
    OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener {
    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var detailsOverlay: DetailsOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = POSE_DETECTION
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private var camera: Camera? = null
    private var exercise: MutableList<String> = mutableListOf()
    private var countTimer: CountDownTimer? = null
    private lateinit var binding: ActivityCameraXlivePreviewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, POSE_DETECTION)
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera_xlive_preview)
        previewView = binding.previewView

        val colorDrawable = ColorDrawable(Color.parseColor("#342c3a"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        // get exercise name
        val bundle = intent.getBundleExtra("exercise")!!
        val exerciseName = bundle.getString("exerciseName")!!
        val reps = bundle.getString("reps")!!
        val sets = bundle.getString("sets")!!

        binding.repsOverlayText.text = "0"
        binding.testRep.text = getString(R.string.reps_overlay_text, reps)

        binding.setsOverlayText.text = "1"
        binding.testSet.text = getString(R.string.sets_overlay_text, sets)

        binding.sideOverlayText.text = getString(R.string.side_overlay_text, "N/A")
        binding.paceOverlayText.text = getString(R.string.pace_overlay_text, "0.0s")
        binding.errorsOverlayText.text = getString(R.string.erros_overlay_text, "0")

        exercise.add(exerciseName)
        supportActionBar?.title = exerciseName

        if (Build.VERSION.SDK_INT >= 21) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.background)
        }

        setupOnCreate()
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(
                this,
                { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (allPermissionsGranted()) {
                        bindAllCameraUseCases()
                    }
                })
        if (!allPermissionsGranted()) {
            runtimePermissions
        }
//        tflite.initialize()
//            .addOnSuccessListener { Log.i("Tflite", "Tflite model initilized successfully") }
//            .addOnFailureListener{ e -> Log.e("Tflite", "Error initializing classifier")}

    }

    private fun startTimer() {
        // start timer
        val timerView = binding.timer
        countTimer = object : CountDownTimer(6 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val calendar = Calendar.getInstance()
                calendar.time = Date(millisUntilFinished)
                val secs = calendar.get(Calendar.SECOND).toString()
                if (secs == "0") {
                    bindAnalysisUseCase()
                } else {
                    timerView.text = if (secs == "0") {
                        ""
                    } else {
                        secs
                    }
                }
            }

            override fun onFinish() {
                timerView.visibility = View.GONE
               binding.timerText.visibility = View.GONE
            }
        }

        countTimer!!.start()
    }

    private fun setupOnCreate() {
        if (previewView == null) {
            Log.d(TAG, "previewView is null")
        }
        graphicOverlay = binding.graphicOverlay
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

//        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
//        facingSwitch.setOnCheckedChangeListener(this)

    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(STATE_SELECTED_MODEL, selectedModel)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (cameraProvider == null) {
            return
        }
        val newLensFacing =
            if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Log.d(
                    TAG,
                    "Set facing to $newLensFacing"
                )
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            // Falls through
        }
        Toast.makeText(
            applicationContext,
            "This device does not have lens with facing: $newLensFacing",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        countTimer?.cancel()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (imageProcessor != null) {
            imageProcessor!!.stop()
            imageProcessor!!.resetInfo(binding)
        }
        countTimer?.cancel()
    }

    private fun clearDetailsOverlay(){
        binding.repsOverlayText.text = "0"
        binding.errorsOverlayText.text = "0"
        binding.setsOverlayText.text = "1"
        binding.paceOverlayText.text = "0.0s"
        binding.sideOverlayText.text = "N/A"
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        if (countTimer == null) {
            startTimer()
        }

        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        camera = cameraProvider!!.bindToLifecycle( /* lifecycleOwner= */this,
            cameraSelector!!, previewUseCase
        )
        attachZoomListener()

    }

    fun addFlash(flash: Boolean) {
        if (camera!!.cameraInfo.hasFlashUnit()) {
            camera!!.cameraControl.enableTorch(flash)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attachZoomListener() {
        // Listen to pinch gestures
        val cameraInfo = camera!!.cameraInfo
        val cameraControl = camera!!.cameraControl

        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Get the camera's current zoom ratio
                val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0F

                // Get the pinch gesture's scaling factor
                val delta = detector.scaleFactor

                // Update the camera's zoom ratio. This is an asynchronous operation that returns
                // a ListenableFuture, allowing you to listen to when the operation completes.
                cameraControl.setZoomRatio(currentZoomRatio * delta)

                // Return true, as the event was handled
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(applicationContext, listener)

        // Attach the pinch gesture listener to the viewfinder
        previewView?.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private fun bindAnalysisUseCase() {
        detailsOverlay = binding.detailsOverlay
        if (detailsOverlay == null) {
            Log.d(TAG, "detailsOverlay is null")
        }

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor = try {
            if (POSE_DETECTION == selectedModel) {
                val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                val shouldShowInFrameLikelihood = true
//                    PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
                val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
                val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
                val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
                PoseDetectorProcessor(
                    this, poseDetectorOptions, visualizeZ, rescaleZ, exercise
                )
            } else {
                throw IllegalStateException("Invalid model name")
            }
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Can not create image processor: $selectedModel", e
            )
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.localizedMessage,
                Toast.LENGTH_LONG
            )
                .show()
            return
        }
        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase!!.setAnalyzer( // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this),
            { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped =
                        lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        graphicOverlay!!.setImageSourceInfo(
                            imageProxy.width, imageProxy.height, isImageFlipped
                        )
                    } else {
                        graphicOverlay!!.setImageSourceInfo(
                            imageProxy.height, imageProxy.width, isImageFlipped
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    imageProcessor!!.processImageProxy(
                        imageProxy,
                        graphicOverlay!!,
                        binding,

                    )
                } catch (e: MlKitException) {
                    Log.e(
                        TAG,
                        "Failed to process image. Error: " + e.localizedMessage
                    )
                    Toast.makeText(
                        applicationContext,
                        e.localizedMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        cameraProvider!!.bindToLifecycle( /* lifecycleOwner= */this,
            cameraSelector!!, analysisUseCase
        )
    }

    private val requiredPermissions: Array<String?>
        private get() = try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                return false
            }
        }
        return true
    }

    private val runtimePermissions: Unit
        private get() {
            val allNeededPermissions: MutableList<String?> = ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(this, permission)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (!allNeededPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        Log.i(TAG, "Permission granted!")
        if (allPermissionsGranted()) {
            bindAllCameraUseCases()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val TAG = "CameraXLivePreview"
        private const val PERMISSION_REQUESTS = 1
        private const val POSE_DETECTION = "Pose Detection"
        private const val STATE_SELECTED_MODEL = "selected_model"
        private fun isPermissionGranted(context: Context, permission: String?): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission!!)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(
                    TAG,
                    "Permission granted: $permission"
                )
                return true
            }
            Log.i(
                TAG,
                "Permission NOT granted: $permission"
            )
            return false
        }
    }
}