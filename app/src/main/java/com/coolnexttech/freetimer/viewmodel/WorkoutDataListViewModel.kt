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

class WorkoutDataListViewModel: ViewModel() {
    private val _workoutDataList = MutableStateFlow<List<WorkoutData>>(listOf())
    val workoutDataList: StateFlow<List<WorkoutData>> = _workoutDataList

    private var workoutDataStorage: WorkoutDataStorage? = null

    fun initDb(context: Context) {
        workoutDataStorage = WorkoutDataStorage.getInstance(context)
        getWorkoutDataList()
    }

    fun deleteWorkoutData(workoutData: WorkoutData) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDataStorage?.delete(workoutData)
            getWorkoutDataList()
        }
    }

    private fun getWorkoutDataList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = workoutDataStorage?.getAll() ?: return@launch

            launch(Dispatchers.Main) {
                _workoutDataList.update {
                    list
                }
            }

        }
    }
}