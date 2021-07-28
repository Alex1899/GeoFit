package com.example.geofitapp.ui.exercisePreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExercisePreviewViewModel : ViewModel() {
    private val _reps = MutableLiveData<Int>()
    private val _sets = MutableLiveData<Int>()
    private val _weight= MutableLiveData<Float>()

    val reps: LiveData<Int>
        get() = _reps

    val sets: LiveData<Int>
        get() = _sets

    val weight: LiveData<Float>
        get() = _weight


    init{
        _reps.value = 12
        _sets.value = 3
        _weight.value = 0f
    }

    fun updateReps(reps: String){
        _reps.value = reps.toInt()
    }

    fun updateSets(sets: String){
        _sets.value = sets.toInt()
    }

    fun updateWeight(weight: String){
        _weight.value = weight.toFloat()
    }
}