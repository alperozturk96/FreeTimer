package com.coolnexttech.freetimer.viewmodel

import androidx.lifecycle.ViewModel
import com.coolnexttech.freetimer.model.WorkoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel: ViewModel() {
    private val _workoutData = MutableStateFlow(WorkoutData())
    val workoutData: StateFlow<WorkoutData> = _workoutData
}