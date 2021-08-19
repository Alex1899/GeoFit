package com.example.geofitapp.ui.exercisePreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExercisePreviewViewModel : ViewModel() {
    private val _reps = MutableLiveData<Int>()
    private val _sets = MutableLiveData<Int>()
    private val _currentSet = MutableLiveData<Int>()
    private val _weight= MutableLiveData<Float>()
    private val _rest = MutableLiveData<Int>()

    private val _exerciseData = MutableLiveData<ExerciseData>()


    val reps: LiveData<Int>
        get() = _reps

    val currentSet: LiveData<Int>
        get() = _currentSet

    val sets: LiveData<Int>
        get() = _sets

    val weight: LiveData<Float>
        get() = _weight

    val rest: LiveData<Int>
        get() = _rest

    val exerciseData: LiveData<ExerciseData>
        get() = _exerciseData


    init{
        _reps.value = 12
        _sets.value = 3
        _currentSet.value = 1
        _weight.value = 0f
        _rest.value = 60

    }

    fun updateReps(reps: String){
        _reps.value = reps.toInt()
    }

    fun updateSets(sets: String){
        _sets.value = sets.toInt()
    }

    fun updateCurrentSet(set: String){
        _currentSet.value = set.toInt()
    }

    fun updateWeight(weight: String){
        _weight.value = weight.toFloat()
    }


    fun updateRest(rest: String){
        _rest.value = rest.toInt()
    }

    fun resetDetails(){
        _reps.value = 12
        _sets.value = 3
        _currentSet.value = 1
        _weight.value = 0f
        _rest.value = 60
    }

    fun saveExerciseData(exerciseData: ExerciseData?){
        if(exerciseData != null){
            _exerciseData.value = exerciseData
        }
    }
}