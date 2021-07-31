package com.example.geofitapp.ui.exerciseSetDetails.lineChart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat


class YaxisValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat = DecimalFormat()

    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}" + "\u00B0"
    }
}
