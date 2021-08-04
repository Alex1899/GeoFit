package com.example.geofitapp.ui.exerciseSetDetails

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityExerciseSetDetailsBinding
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

        val charts = mutableListOf<LineChart>()
        for (triple in exerciseSetDetails.angleList) {
            val angleListY = triple.second
            val pair1 = Pair(max(angleListY).toFloat(), min(angleListY).toFloat())
            Log.i("SetDetails", "${triple.first} size = ${triple.second.size}")
            val angleListX = (0..angleListY.size).toList()
            val entryList = angleListX.zip(angleListY) { x, y -> Entry(x.toFloat(), y.toFloat()) }

            charts.add(
                AnglesLineChart.initilise(
                    entryList,
                    triple.first,
                    triple.third,
                    pair1,
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
    }


//
//    override fun onBackPressed() {
//        Log.d("CDA", "onBackPressed Called")
//
//
//    }
}