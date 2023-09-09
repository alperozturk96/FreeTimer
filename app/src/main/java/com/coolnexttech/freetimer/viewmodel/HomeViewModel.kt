package com.coolnexttech.freetimer.viewmodel

import androidx.lifecycle.ViewModel
import com.coolnexttech.freetimer.model.WorkoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel: ViewModel() {
    private val _workoutData = MutableStateFlow(WorkoutData(id = 1))
    val workoutData: StateFlow<WorkoutData> = _workoutData

    private val _showSaveWorkoutAlert = MutableStateFlow(false)
    val showSaveWorkoutAlert: StateFlow<Boolean> = _showSaveWorkoutAlert

    fun showSaveWorkoutAlert() {
        _showSaveWorkoutAlert.update {
            true
        }
    }

    fun hideSaveWorkoutAlert() {
        _showSaveWorkoutAlert.update {
            false
        }
    }

    fun saveWorkout() {

        hideSaveWorkoutAlert()
    }
}