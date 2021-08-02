package com.example.geofitapp.ui.exerciseSetDetails

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.geofitapp.databinding.ActivityExerciseSetDetailsBinding
import com.example.geofitapp.databinding.ItemChartViewBinding
import com.github.mikephil.charting.charts.LineChart

class ChartAdapter(
    private val context: Context,
    private val chartList: List<LineChart>
) : RecyclerView.Adapter<ChartAdapter.ViewHolder>() {


    class ViewHolder private constructor(val binding: ItemChartViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chart: LineChart) {
            binding.chartFrameLayout.addView(chart)
            chart.animateX(1500)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChartViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chart = chartList[position]
        holder.bind(chart)
    }

    override fun getItemCount() = chartList.size

}