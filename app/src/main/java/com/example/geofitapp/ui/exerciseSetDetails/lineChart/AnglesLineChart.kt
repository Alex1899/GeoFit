package com.example.geofitapp.ui.exerciseSetDetails.lineChart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class AnglesLineChart(private val chart: LineChart, entryList: List<Entry>, context: Context) {

    init {
        chart.isAutoScaleMinMaxEnabled = true

        val dataSet = LineDataSet(entryList, "Sequence of elbow angles")

        dataSet.lineWidth = 2.5f
        dataSet.setDrawCircles(false)
//        dataSet.circleRadius = 3f
        dataSet.highLightColor = Color.rgb(244, 117, 117)
        dataSet.color = ColorTemplate.VORDIPLOM_COLORS[0]
        dataSet.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        val mTf = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")

        val ll1 = LimitLine(138f, "Max Threshold")
        ll1.lineWidth = 2f

        ll1.enableDashedLine(40f, 20f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        ll1.textSize = 10f
        ll1.textColor = Color.WHITE
        // set this color programatically, i.e red is value is above
        ll1.lineColor = Color.GREEN

        val ll2 = LimitLine(68f, "Min Threshold")
        ll2.lineWidth = 2f
        ll2.enableDashedLine(40f, 20f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        ll2.textSize = 10f
        ll2.textColor = Color.WHITE
        ll2.lineColor = Color.GREEN


        // draw limit lines behind data instead of on top
//        chart.axisLeft.setDrawLimitLinesBehindData(true)
//        chart.xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines

        val custom: ValueFormatter = YaxisValueFormatter()


        // add limit lines
        chart.axisLeft.addLimitLine(ll1)
        chart.axisLeft.addLimitLine(ll2)
        chart.axisLeft.isInverted = true
        chart.axisLeft.axisMinimum = 0f

        chart.axisLeft.valueFormatter = custom

        chart.xAxis.isEnabled = false

        chart.data = lineData
        chart.setNoDataTextColor(Color.WHITE)
//        chart.xAxis.textColor = Color.WHITE
//        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        chart.xAxis.setDrawGridLines(false)
//        chart.xAxis.setDrawAxisLine(true)
//        chart.xAxis.typeface = mTf
        chart.axisLeft.typeface = mTf
        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.isEnabled = false

        chart.setDrawGridBackground(false)

        //chart.invalidate()
        chart.animateX(1500)
    }


}