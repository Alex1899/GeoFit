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
        val map = mutableMapOf( "Dumbbell Bicep Curl" to Pair(R.raw.bicep_curl_tut, R.drawable.bicep_vid_thumbnail))
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
                val exerciseData = ExerciseData(map[it]!!.first, map[it]!!.second, it ,getString(R.string.bicep_curl_desc))
                findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToExercisePreviewFragment(exerciseData))
                viewModel.doneNavigating()
//                val intent = Intent(activity, CameraXLivePreviewActivity::class.java)
//                intent.putExtra("exerciseName", it)
//                activity?.startActivity(intent)

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