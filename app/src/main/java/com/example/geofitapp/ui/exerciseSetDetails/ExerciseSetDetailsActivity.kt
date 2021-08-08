package com.example.geofitapp.ui.exerciseSetDetails

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityExerciseSetDetailsBinding
import com.example.geofitapp.posedetection.poseDetector.PoseDetectorProcessor
import com.example.geofitapp.posedetection.poseDetector.exerciseProcessor.ExerciseProcessor
import com.example.geofitapp.posedetection.poseDetector.jointAngles.ExerciseUtils
import com.example.geofitapp.ui.exercisePreview.ExercisePreviewViewModel
import com.example.geofitapp.ui.exercisePreview.ViewModelFactory
import com.example.geofitapp.ui.exerciseSetDetails.lineChart.AnglesLineChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import java.util.Collections.max
import java.util.Collections.min


class ExerciseSetDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseSetDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_set_details)

        val exerciseSetDetails =
            intent.getParcelableExtra<ExerciseSetDetails>("exerciseSetDetails")!!
        binding.exerciseSetDetails = exerciseSetDetails

        binding.restartExercise.setOnClickListener {
            val previewViewModel = ViewModelProvider(
                this, ViewModelFactory()
            ).get(ExercisePreviewViewModel::class.java)
            val set = if (exerciseSetDetails.currentSet == exerciseSetDetails.sets) {
                1
            } else {
                exerciseSetDetails.currentSet + 1
            }
            previewViewModel.updateCurrentSet(set.toString())
            ExerciseProcessor.resetDetails()
            onBackPressed()
        }

        val charts = mutableListOf<LineChart>()
        for (triple in exerciseSetDetails.angleList) {
            val angleListPair = triple.second
            val angleListY = angleListPair.first
            var angleListY2: MutableList<Double>?
            var pair2:  Pair<Float, Float>? = null
            var entryList2:  List<Entry>? = null

            val angleListX = (0..angleListY.size).toList()

            if(angleListPair.second !== null){
                angleListY2 = angleListPair.second!!
                pair2 = Pair(max(angleListY2).toFloat(), min(angleListY2).toFloat())
                entryList2 = angleListX.zip(angleListY2) { x, y -> Entry(x.toFloat(), y.toFloat()) }

            }
            val pair1 = Pair(max(angleListY).toFloat(), min(angleListY).toFloat())
            val entryList = angleListX.zip(angleListY) { x, y -> Entry(x.toFloat(), y.toFloat()) }

            charts.add(
                AnglesLineChart.initilise(
                    Pair(entryList,entryList2),
                    triple.first,
                    triple.third,
                    Pair(pair1,pair2),
                    ExerciseUtils.isYaxisInverted[exerciseSetDetails.exerciseName]!!,
                    this
                )
            )
        }
        val recyclerView = binding.chartRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ChartAdapter(this, charts)

        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(CirclePagerIndicatorDecoration())

        val feedbackRecyclerView = binding.feedbackRecyclerView
        feedbackRecyclerView.setHasFixedSize(true)
        feedbackRecyclerView.adapter = FeedbackAdapter(
            this,
            getFeedback(exerciseSetDetails),
            getIncorrectRepsList(exerciseSetDetails)
        )

        feedbackRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )

        val snapHelper2 = PagerSnapHelper()
        snapHelper2.attachToRecyclerView(feedbackRecyclerView)
        feedbackRecyclerView.addItemDecoration(CirclePagerIndicatorDecoration())
    }

    private fun getFeedback(exerciseSetDetails: ExerciseSetDetails):
            MutableMap<String, MutableMap<String, Pair<String, String>>> {
        val feedbackList = exerciseSetDetails.feedback.values.toList()
        return feedbackList.first()
    }

    private fun getIncorrectRepsList(exerciseSetDetails: ExerciseSetDetails): MutableMap<String, MutableMap<String, Pair<MutableList<Int>, String>>> {
        val repsMap =
            mutableMapOf<String, MutableMap<String, Pair<MutableList<Int>, String>>>()
        for ((rep, map) in exerciseSetDetails.feedback) {
            for ((key, feedbackMap) in map) {
                for ((aoiKey, fMap) in feedbackMap) {
                    val feedbackText = fMap.second
                    if (fMap.first == "Wrong") {
                        if (!repsMap.containsKey(key)) {
                            repsMap[key] =
                                mutableMapOf(aoiKey to Pair(mutableListOf(rep), feedbackText))
                        } else {
                            if (!repsMap[key]!!.containsKey(aoiKey)) {
                                repsMap[key]!![aoiKey] = Pair(mutableListOf(rep), feedbackText)
                            } else {
                                repsMap[key]!![aoiKey]!!.first.add(rep)
                                repsMap[key]!![aoiKey] =
                                    repsMap[key]!![aoiKey]!!.copy(second = feedbackText)
                            }

                        }
                    } else {
                        if (!repsMap.containsKey(key)) {
                            repsMap[key] =
                                mutableMapOf(aoiKey to Pair(mutableListOf(), feedbackText))
                        } else {
                            if (!repsMap[key]!!.containsKey(aoiKey)) {
                                repsMap[key]!![aoiKey] = Pair(mutableListOf(), feedbackText)
                            }
                        }
                    }
                }

            }
        }
        return repsMap
    }


//
//    override fun onBackPressed() {
//        Log.d("CDA", "onBackPressed Called")
//
//
//    }
}