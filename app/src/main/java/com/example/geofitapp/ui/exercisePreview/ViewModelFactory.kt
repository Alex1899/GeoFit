package com.example.geofitapp.ui.exercisePreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisePreviewViewModel::class.java)) {
            val key = "UserProfileViewModel"
            return if(hashMapViewModel.containsKey(key)){
                getViewModel(key) as T
            } else {
                addViewModel(key, ExercisePreviewViewModel())
                getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel){
            hashMapViewModel[key] = viewModel
        }
        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }
}