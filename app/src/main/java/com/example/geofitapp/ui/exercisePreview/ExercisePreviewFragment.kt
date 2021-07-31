package com.example.geofitapp.ui.exercisePreview

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.geofitapp.R
import com.example.geofitapp.databinding.FragmentExercisePreviewBinding
import com.example.geofitapp.ui.cameraPreview.CameraXLivePreviewActivity
import com.example.geofitapp.ui.home.HomePageViewModel


class ExercisePreviewFragment : Fragment() {
    private lateinit var binding: FragmentExercisePreviewBinding
    private lateinit var videoPath: String
    private var imagePath: Int? = null
    private val viewModel: ExercisePreviewViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_exercise_preview, container, false
        )
        val arguments = ExercisePreviewFragmentArgs.fromBundle(requireArguments())
        binding.exerciseData = arguments.exerciseData
        videoPath =
            "android.resource://" + requireActivity().packageName + "/" + arguments.exerciseData.video
        imagePath = arguments.exerciseData.videoThumbnail

        binding.repsEditText.setText(viewModel.reps.value.toString())
        binding.setsEditText.setText(viewModel.sets.value.toString())
        binding.weightEditText.setText(viewModel.weight.value.toString())
        binding.restEditText.setText(viewModel.rest.value.toString())


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thumbnailView = binding.videoViewThumbnail
        Glide.with(requireActivity()).load(imagePath!!).into(thumbnailView);

        val videoView = binding.videoView
        videoView.setVideoPath(videoPath)
        val mediaController = MediaController(requireContext())

        mediaController.setAnchorView(videoView)

        videoView.setOnCompletionListener {
            videoView.stopPlayback()
            binding.videoBackground.visibility = View.VISIBLE
        }

        binding.playBtn.setOnClickListener {
            binding.videoBackground.visibility = View.GONE
            videoView.setMediaController(mediaController)
            videoView.start()
        }

        binding.repsEditText.addTextChangedListener {
            if (it.toString() != "") {
                viewModel.updateReps(it.toString())
            }
        }
        binding.setsEditText.addTextChangedListener {
            if (it.toString() != "") {
                viewModel.updateSets(it.toString())
            }
        }
        binding.weightEditText.addTextChangedListener {
            if (it.toString() != "") {
                viewModel.updateWeight(it.toString())
            }
        }

        binding.restEditText.addTextChangedListener {
            if (it.toString() != "") {
                viewModel.updateRest(it.toString())
            }
        }

        binding.repsMinusIcon.setOnClickListener {
            if (viewModel.reps.value!! > 0) {
                binding.repsEditText.setText((viewModel.reps.value!! - 1).toString())
            }
        }

        binding.repsPlusIcon.setOnClickListener {
            binding.repsEditText.setText((viewModel.reps.value!! + 1).toString())
        }

        binding.setsMinusIcon.setOnClickListener {
            if (viewModel.sets.value!! > 0) {
                binding.setsEditText.setText((viewModel.sets.value!! - 1).toString())
            }
        }

        binding.setsPlusIcon.setOnClickListener {
            binding.setsEditText.setText((viewModel.sets.value!! + 1).toString())
        }

        binding.weightMinusIcon.setOnClickListener {
            if (viewModel.weight.value!! > 0) {
                binding.weightEditText.setText((viewModel.weight.value!! - 1).toString())
            }
        }

        binding.weightPlusIcon.setOnClickListener {
            binding.weightEditText.setText((viewModel.weight.value!! + 1).toString())
        }

        binding.restMinusIcon.setOnClickListener {
            if (viewModel.rest.value!! > 0) {
                binding.restEditText.setText((viewModel.rest.value!! - 1).toString())
            }
        }

        binding.restPlusIcon.setOnClickListener {
            binding.restEditText.setText((viewModel.rest.value!! + 1).toString())
        }

        binding.startExercise.setOnClickListener {
            if (viewModel.reps.value!! > 0 && viewModel.sets.value!! > 0) {
                val intent = Intent(activity, CameraXLivePreviewActivity::class.java)
                val bundle = Bundle()
                bundle.putString("exerciseName", binding.exerciseData!!.exerciseName)
                bundle.putString("reps", viewModel.reps.value.toString())
                bundle.putString("sets", viewModel.sets.value.toString())

                intent.putExtra("exercise", bundle)
                activity?.startActivity(intent)
            } else {
                Toast.makeText(
                    activity,
                    "Make sure to add the number of reps and sets!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}