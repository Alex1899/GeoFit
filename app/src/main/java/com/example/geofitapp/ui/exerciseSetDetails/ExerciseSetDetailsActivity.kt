package com.example.geofitapp.ui.exerciseSetDetails

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ActivityExerciseSetDetailsBinding
import com.example.geofitapp.ui.exerciseSetDetails.lineChart.AnglesLineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class ExerciseSetDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseSetDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_set_details)
        val exerciseSetDetails = intent.getParcelableExtra<ExerciseSetDetails>("exerciseSetDetails")!!

        val chart = binding.chart
        val angleListY = exerciseSetDetails.angleList
        val angleListX = (0..angleListY.size).toList()

        val entryList = angleListX.zip(angleListY){ x, y -> Entry(x.toFloat(), y.toFloat())}
        val chartClass = AnglesLineChart(chart, entryList, this)

    }

    override fun onBackPressed() {
        Log.d("CDA", "onBackPressed Called")


    }
}