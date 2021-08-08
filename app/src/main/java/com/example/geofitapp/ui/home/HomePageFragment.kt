package com.example.geofitapp.ui.home


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.geofitapp.R
import com.example.geofitapp.databinding.FragmentHomePageBinding
import com.example.geofitapp.ui.cameraPreview.CameraXLivePreviewActivity
import com.example.geofitapp.ui.exercisePreview.ExerciseData

class HomePageFragment : Fragment() {
    private lateinit var binding: FragmentHomePageBinding
    private val viewModel: HomePageViewModel by viewModels()

    companion object{
        val map = mutableMapOf(
            "Dumbbell Bicep Curl" to Triple(R.raw.bicep_curl_tut, R.drawable.bicep_curl_thumb, R.string.bicep_curl_desc),
            "Triceps Pushdown" to Triple(R.raw.triceps_pushdown_tut, R.drawable.triceps_pushdown_thumb, R.string.triceps_pushdown_desc),
            "Shoulder Press" to Triple(R.raw.shoulder_press_tut, R.drawable.shoulder_press_thumb, R.string.shoulder_press_desc),
            "Front Raise" to Triple(R.raw.front_raise_tut, R.drawable.front_raise_thumb, R.string.front_raise_desc)
        )

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.exerciseName.observe(viewLifecycleOwner, {
            if(it !== ""){
                val exerciseData = ExerciseData(map[it]!!.first, map[it]!!.second, it ,getString(map[it]!!.third))
                findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToExercisePreviewFragment(exerciseData))
                viewModel.doneNavigating()

            }
        })

        binding.bicepCurlCardview.setOnClickListener {
            viewModel.updateExerciseName(binding.bicepCurlText.text.toString())
        }
        binding.tricepsPushdownCardview.setOnClickListener {
            viewModel.updateExerciseName(binding.tricepsPushdownText.text.toString())
        }
        binding.shoulderPressCardview.setOnClickListener {
            viewModel.updateExerciseName(binding.shoulderPressText.text.toString())
        }
        binding.frontRaiseCardview.setOnClickListener {
            viewModel.updateExerciseName(binding.frontRaiseText.text.toString())
        }


        return binding.root
    }

}