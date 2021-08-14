package com.example.geofitapp.ui.exerciseSetDetails.lineChart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.FrameLayout
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


object AnglesLineChart {

    fun initilise(
        entryList: Pair<List<Entry>, List<Entry>?>,
        chartLabel: Pair<String, String?>,
        limitPair: List<Triple<Float?, Float?, Boolean>>,
        minMaxPair: Pair<Pair<Float, Float>, Pair<Float, Float>?>,
        isYinverted: Boolean,
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

        val dataSet = LineDataSet(entryList.first, chartLabel.first)
        var dataSet2: LineDataSet? = null

        if (entryList.second !== null) {
            dataSet2 = LineDataSet(entryList.second!!, chartLabel.second!!)
            dataSet2.lineWidth = 2.5f
            dataSet2.setDrawCircles(false)
//        dataSet.circleRadius = 3f
            dataSet2.isHighlightEnabled = false
            dataSet2.highLightColor = Color.rgb(244, 117, 117)
            dataSet2.color = ColorTemplate.MATERIAL_COLORS[1]
            dataSet2.setCircleColor(ColorTemplate.MATERIAL_COLORS[1])
            dataSet2.setDrawValues(false)
        }

        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(false)
//        dataSet.circleRadius = 3f
        dataSet.isHighlightEnabled = false
        dataSet.highLightColor = Color.rgb(244, 117, 117)
        dataSet.color = ColorTemplate.MATERIAL_COLORS[2]
        dataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[2])
        dataSet.setDrawValues(false)


        val lineData = if(dataSet2 !== null) LineData(dataSet, dataSet2) else LineData(dataSet)

        val mTf = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

        // chart min and max values
        val maxValueExtremasTriple = limitPair[0]
        val minValueExtremasTriple = limitPair[1]



        val val1 = kotlin.math.abs(minMaxPair.first.second - minValueExtremasTriple.second!!)
        val val2 = kotlin.math.abs(minMaxPair.first.first - maxValueExtremasTriple.first!!)
        var maxVal = val1.coerceAtLeast(val2)
        if (maxVal <= 5) {
            maxVal += 20
        }

        val max = minMaxPair.first.first.coerceAtLeast(maxValueExtremasTriple.first!!) + maxVal
        val min = minMaxPair.first.second.coerceAtMost(minValueExtremasTriple.second!!) - maxVal


        val llMaxForMax = LimitLine(maxValueExtremasTriple.first!!, "Max Threshold For Max Angle")

        val llMaxForMaxColor =
            if (minMaxPair.first.first >= maxValueExtremasTriple.first!! && maxValueExtremasTriple.third) {
                Color.GREEN
            } else if (minMaxPair.first.first >= maxValueExtremasTriple.first!! && !maxValueExtremasTriple.third) {
                Color.RED
            } else {
                if (!maxValueExtremasTriple.third) {
                    Color.GREEN
                } else {
                    Color.RED
                }
            }
        llMaxForMax.lineWidth = 2f
        llMaxForMax.enableDashedLine(40f, 20f, 0f)
        llMaxForMax.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        llMaxForMax.textSize = 10f
        llMaxForMax.textColor = Color.WHITE
        // set this color programatically, i.e red is value is above
        llMaxForMax.lineColor = llMaxForMaxColor

        var llMinForMax: LimitLine? = null
        if (maxValueExtremasTriple.second !== null) {
            llMinForMax = LimitLine(maxValueExtremasTriple.second!!, "Min Threshold For Max Angle")

            val llMinForMaxColor = if (minMaxPair.first.first >= maxValueExtremasTriple.second!!) {
                Color.GREEN
            } else {
                Color.RED
            }
            llMinForMax.lineWidth = 2f
            llMinForMax.enableDashedLine(40f, 20f, 0f)
            llMinForMax.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            llMinForMax.textSize = 10f
            llMinForMax.textColor = Color.WHITE
            // set this color programatically, i.e red is value is above
            llMinForMax.lineColor = llMinForMaxColor
        }


        val llMinForMin = LimitLine(minValueExtremasTriple.second!!, "Min Threshold For Min Angle")
        val llMinForMinColor =
            if (minMaxPair.first.second <= minValueExtremasTriple.second!! && minValueExtremasTriple.third) {
                Color.GREEN
            } else if (minMaxPair.first.second <= minValueExtremasTriple.second!! && !minValueExtremasTriple.third) {
                Color.RED
            } else {
                if (minValueExtremasTriple.third) {
                    Color.RED
                } else {
                    Color.GREEN
                }
            }

        llMinForMin.lineWidth = 2f
        llMinForMin.enableDashedLine(40f, 20f, 0f)
        llMinForMin.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        llMinForMin.textSize = 10f
        llMinForMin.textColor = Color.WHITE
        llMinForMin.lineColor = llMinForMinColor


        var llMaxForMin: LimitLine? = null
        if (minValueExtremasTriple.first !== null) {
            llMaxForMin = LimitLine(minValueExtremasTriple.first!!, "Max Threshold For Min Angle")

            val llMaxForMinColor = if (minMaxPair.first.second >= minValueExtremasTriple.first!!) {
                Color.RED
            } else {
                Color.GREEN
            }
            llMaxForMin.lineWidth = 2f
            llMaxForMin.enableDashedLine(40f, 20f, 0f)
            llMaxForMin.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            llMaxForMin.textSize = 10f
            llMaxForMin.textColor = Color.WHITE
            llMaxForMin.lineColor = llMaxForMinColor
        }


        // draw limit lines behind data instead of on top
//        chart.axisLeft.setDrawLimitLinesBehindData(true)
//        chart.xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines

        val custom: ValueFormatter = YaxisValueFormatter()


        // add limit lines
        chart.axisLeft.apply {
            addLimitLine(llMaxForMax)
            addLimitLine(llMinForMin)

            if (llMinForMax !== null) {
                addLimitLine(llMinForMax)
            }

            if (llMaxForMin !== null) {
                addLimitLine(llMaxForMin)
            }
        }


        chart.axisLeft.isInverted = isYinverted
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
        chart.axisRight.isInverted = isYinverted
        chart.axisRight.typeface = mTf
        chart.axisRight.axisMinimum = min
        chart.axisRight.axisMaximum = max
        chart.axisRight.setDrawGridLines(false)

        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)

        val l = chart.legend
//        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//        l.orientation = Legend.LegendOrientation.VERTICAL
        l.textColor = Color.WHITE
        l.setDrawInside(true)

        //chart.invalidate()
//        chart.animateX(1500)

        return chart
    }


}