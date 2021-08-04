package com.example.geofitapp.ui.exerciseSetDetails.lineChart

import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import com.example.geofitapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.lang.Math.*


object AnglesLineChart {

    fun initilise(
        entryList: List<Entry>,
        chartLabel: String,
        limitPair: Triple<Float, Float, Boolean>,
        minMaxPair: Pair<Float, Float>,
        context: Context
    ): LineChart {

        val chart = LineChart(context)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        chart.layoutParams = params


//        chart.isAutoScaleMinMaxEnabled = true
        chart.setBackgroundColor(getColor(context, R.color.primaryColor))

        val dataSet = LineDataSet(entryList, chartLabel)

        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(false)
//        dataSet.circleRadius = 3f
        dataSet.isHighlightEnabled = false
        dataSet.highLightColor = Color.rgb(244, 117, 117)
        dataSet.color = ColorTemplate.VORDIPLOM_COLORS[0]
        dataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        val mTf = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

        // chart min and max values
        val val1 = kotlin.math.abs(minMaxPair.second - limitPair.second)
        val val2 = kotlin.math.abs(minMaxPair.first - limitPair.first)
        val maxVal = val1.coerceAtLeast(val2)

        val max = minMaxPair.first.coerceAtLeast(limitPair.first) + maxVal
        val min = minMaxPair.second.coerceAtMost(limitPair.second) - maxVal

        val ll1 = LimitLine(limitPair.first, "Max Threshold")
        val ll1Color = if (minMaxPair.first >= limitPair.first && !limitPair.third) {
            Color.GREEN
        } else if (minMaxPair.first >= limitPair.first && limitPair.third) {
            Color.RED
        } else {
            if (!limitPair.third) {
                Color.RED
            } else {
                Color.GREEN
            }
        }
        ll1.lineWidth = 2f
        ll1.enableDashedLine(40f, 20f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        ll1.textSize = 10f
        ll1.textColor = Color.WHITE
        // set this color programatically, i.e red is value is above
        ll1.lineColor = ll1Color


        val ll2 = LimitLine(limitPair.second, "Min Threshold")
        val ll2Color = if (minMaxPair.second <= limitPair.second && !limitPair.third) {
            Color.GREEN
        } else if (minMaxPair.second <= limitPair.second && limitPair.third) {
            Color.RED
        } else {
            if (!limitPair.third) {
                Color.RED
            } else {
                Color.GREEN
            }
        }

        ll2.lineWidth = 2f
        ll2.enableDashedLine(40f, 20f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        ll2.textSize = 10f
        ll2.textColor = Color.WHITE
        ll2.lineColor = ll2Color


        // draw limit lines behind data instead of on top
//        chart.axisLeft.setDrawLimitLinesBehindData(true)
//        chart.xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines

        val custom: ValueFormatter = YaxisValueFormatter()


        // add limit lines
        chart.axisLeft.addLimitLine(ll1)
        chart.axisLeft.addLimitLine(ll2)

        chart.axisLeft.isInverted = true
        chart.axisLeft.axisMinimum = min
        chart.axisLeft.axisMaximum = max
        chart.axisLeft.valueFormatter = custom
        chart.axisLeft.setDrawGridLines(false)

        chart.data = lineData
        chart.setNoDataTextColor(Color.WHITE)

//        chart.xAxis.textColor = Color.WHITE
        chart.xAxis.setDrawLabels(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(true)
//        chart.xAxis.typeface = mTf

        chart.axisLeft.typeface = mTf
        chart.axisLeft.textColor = Color.WHITE

        chart.axisRight.valueFormatter = custom
        chart.axisRight.textColor = Color.WHITE
        chart.axisRight.isInverted = true
        chart.axisRight.typeface = mTf
        chart.axisRight.axisMinimum = min
        chart.axisRight.axisMaximum = max
        chart.axisRight.setDrawGridLines(false)

        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.textColor = Color.WHITE

        //chart.invalidate()
//        chart.animateX(1500)

        return chart
    }


}