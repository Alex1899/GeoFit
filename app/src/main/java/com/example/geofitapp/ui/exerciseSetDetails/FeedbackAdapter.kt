package com.example.geofitapp.ui.exerciseSetDetails

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ItemFeedbackViewBinding

class FeedbackAdapter(
    private val context: Context,
    private val feedbackMap:
    MutableMap<String, MutableMap<String, Pair<String, String>>>,
    private val incorrectReps: MutableMap<String, MutableMap<String, Pair<MutableList<Int>, MutableList<Int>>>>

) : RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: ItemFeedbackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            map: MutableMap<String, Pair<String, String>>,
            key: String,
            incorrectReps: MutableMap<String, MutableMap<String, Pair<MutableList<Int>, MutableList<Int>>>>,
            context: Context
        ) {
            val titleTV = TextView(context)
            titleTV.text = key
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 20 }
            titleTV.layoutParams = params
            titleTV.textAlignment = View.TEXT_ALIGNMENT_CENTER
            titleTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            titleTV.setTextColor(Color.WHITE)

            // position title
            binding.feedbackLinearLayout.addView(titleTV)

            for ((k, v) in map) {
                val ll = getAoiLabel(context, k, incorrectReps[key]!![k]!!)
                binding.feedbackLinearLayout.addView(ll)
            }


            // aoi labels

        }

        private fun getAoiLabel(
            context: Context,
            text: String,
            reps: Pair<MutableList<Int>, MutableList<Int>>
        ): LinearLayout {
            val linearLayout = LinearLayout(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                bottomMargin = 20
                gravity = Gravity.CENTER_VERTICAL
            }

            linearLayout.layoutParams = params
            linearLayout.isBaselineAligned = false
            linearLayout.background =
                ContextCompat.getDrawable(context, R.drawable.aoi_text_overlay)

            val aoiTV = TextView(context)
            val txtParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { weight = 1f }

            aoiTV.layoutParams = txtParams
            aoiTV.text = text

            aoiTV.setTextColor(Color.WHITE)
            aoiTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)

            val errorTV = TextView(context)
            val errParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            errorTV.layoutParams = errParams
            val errtext: String
            val color: Int

            if (reps.second.isEmpty()) {
                errtext = "No Mistakes"
                color = Color.GREEN
            } else {
                errtext = "Mistakes in reps: " + reps.second.joinToString()
                color = Color.RED
            }
            errorTV.text = errtext
            errorTV.setTextColor(color)
            errorTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)


            // append aoi text and error reps count
            linearLayout.addView(aoiTV)
            linearLayout.addView(errorTV)

            return linearLayout

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFeedbackViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = feedbackMap.keys.toList()[position]
        val map = feedbackMap[key]!!
        holder.bind(map, key, incorrectReps, context)
    }

    override fun getItemCount() = feedbackMap.keys.size

}

