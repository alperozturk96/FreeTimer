package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.db.WorkoutDataStorage
import com.coolnexttech.freetimer.model.WorkoutData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _workoutData = MutableStateFlow(WorkoutData())
    val workoutData: StateFlow<WorkoutData> = _workoutData

    private val _showSaveWorkoutAlert = MutableStateFlow(false)
    val showSaveWorkoutAlert: StateFlow<Boolean> = _showSaveWorkoutAlert

    private var workoutDataStorage: WorkoutDataStorage? = null

    fun initDb(context: Context) {
        workoutDataStorage = WorkoutDataStorage.getInstance(context)
    }

    fun showSaveWorkoutAlert() {
        _showSaveWorkoutAlert.update {
            true
        }
    }

    fun updateWorkoutDataName(name: String) {
        _workoutData.update {
            it.copy(name = name)
        }
    }

    fun hideSaveWorkoutAlert() {
        _showSaveWorkoutAlert.update {
            false
        }
    }

    fun saveWorkout() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDataStorage?.add(_workoutData.value)

            launch(Dispatchers.Main) {
                _workoutData.update {
                    WorkoutData()
                }
                hideSaveWorkoutAlert()
            }
        }
    }
}