package com.example.geofitapp.ui.exercisePreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExercisePreviewViewModel : ViewModel() {
    private val _reps = MutableLiveData<Int>()
    private val _sets = MutableLiveData<Int>()
    private val _weight= MutableLiveData<Float>()
    private val _rest = MutableLiveData<Int>()


    val reps: LiveData<Int>
        get() = _reps

    val sets: LiveData<Int>
        get() = _sets

    val weight: LiveData<Float>
        get() = _weight

    val rest: LiveData<Int>
        get() = _rest


    init{
        _reps.value = 12
        _sets.value = 3
        _weight.value = 0f
        _rest.value = 60

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


    fun updateRest(rest: String){
        _rest.value = rest.toInt()
    }
}