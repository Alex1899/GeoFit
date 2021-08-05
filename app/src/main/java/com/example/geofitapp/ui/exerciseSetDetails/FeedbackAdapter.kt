package com.example.geofitapp.ui.exerciseSetDetails

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.example.geofitapp.R
import com.example.geofitapp.databinding.ItemFeedbackViewBinding

class FeedbackAdapter(
    private val context: Context,
    private val feedbackMap:
    MutableMap<String, MutableMap<String, Pair<String, String>>>,
    private val incorrectReps: MutableMap<String, MutableMap<String, Pair<MutableList<Int>, String>>>

) : RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: ItemFeedbackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            map: MutableMap<String, Pair<String, String>>,
            key: String,
            incorrectReps: MutableMap<String, MutableMap<String, Pair<MutableList<Int>, String>>>,
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

            for ((k, _) in map) {
                val ll = getAoiLabel(context, k, incorrectReps[key]!![k]!!)
                binding.feedbackLinearLayout.addView(ll)
            }
        }

        private fun getAoiLabel(
            context: Context,
            text: String,
            reps: Pair<MutableList<Int>, String>
        ): LinearLayout {
            val mainLinearLayout = LinearLayout(context)
            val mainparams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { bottomMargin = 20 }
            mainLinearLayout.layoutParams = mainparams
            mainLinearLayout.orientation = LinearLayout.VERTICAL
            mainLinearLayout.background =
                ContextCompat.getDrawable(context, R.drawable.aoi_text_overlay)

            val linearLayout = LinearLayout(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
            }

            linearLayout.layoutParams = params
            linearLayout.isBaselineAligned = false


            val aoiTV = TextView(context)
            val txtParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

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

            if (reps.first.isEmpty()) {
                errtext = "No Mistakes"
                color = Color.GREEN
            } else {
                errtext = "Mistakes in reps:\n" + reps.first.joinToString()
                color = Color.RED
            }
            errorTV.text = errtext
            errorTV.gravity = Gravity.CENTER
            errorTV.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            errorTV.setTextColor(color)
            errorTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)

            val feedbackTV = TextView(context)
            feedbackTV.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            feedbackTV.text = reps.second
            feedbackTV.setTextColor(Color.WHITE)
            feedbackTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            feedbackTV.visibility = View.GONE

            val icon = ImageView(context)
            icon.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { marginEnd = 20 }
            icon.setImageResource(R.drawable.ic_expand_down)

            val iconAoi = LinearLayout(context)
            iconAoi.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                weight = 1f
                gravity = Gravity.CENTER_VERTICAL
            }

            iconAoi.addView(icon)
            iconAoi.addView(aoiTV)

            linearLayout.setOnClickListener{
                if(feedbackTV.visibility == View.GONE){
                    feedbackTV.visibility = View.VISIBLE
                    icon.setImageResource(R.drawable.ic_expand_up)
                }else{
                    feedbackTV.visibility = View.GONE
                    icon.setImageResource(R.drawable.ic_expand_down)
                }

            }

            // append aoi text and error reps count
            linearLayout.addView(iconAoi)
            linearLayout.addView(errorTV)

            mainLinearLayout.addView(linearLayout)
            mainLinearLayout.addView(feedbackTV)
            return mainLinearLayout

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

