package com.example.geofitapp.ui.exerciseSetDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExerciseSetDetailsViewModel : ViewModel() {
    private val _setDetails = MutableLiveData<ExerciseSetDetails>()

    val setDetails: LiveData<ExerciseSetDetails>
        get() = _setDetails

    fun saveSetDetails(sd: ExerciseSetDetails){
        _setDetails.value = sd
    }
}