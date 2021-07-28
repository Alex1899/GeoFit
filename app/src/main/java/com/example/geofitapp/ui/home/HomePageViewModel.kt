package com.example.geofitapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomePageViewModel : ViewModel() {

    private val _exerciseName = MutableLiveData<String>()

    init {
        _exerciseName.value = ""
    }

    val exerciseName: LiveData<String>
        get() = _exerciseName


    fun updateExerciseName(name: String) {
        _exerciseName.value = name
    }

    fun doneNavigating(){
        _exerciseName.value = ""
    }

}