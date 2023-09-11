package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.db.CountdownDataStorage
import com.coolnexttech.freetimer.model.CountdownData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _countdownData = MutableStateFlow(CountdownData())
    val countdownData: StateFlow<CountdownData> = _countdownData

    private val _showSaveWorkoutAlert = MutableStateFlow(false)
    val showSaveWorkoutAlert: StateFlow<Boolean> = _showSaveWorkoutAlert

    private var countdownDataStorage: CountdownDataStorage? = null

    fun initDb(context: Context) {
        countdownDataStorage = CountdownDataStorage.getInstance(context)
    }

    fun showSaveWorkoutAlert() {
        _showSaveWorkoutAlert.update {
            true
        }
    }

    fun updateWorkoutDataName(name: String) {
        _countdownData.update {
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
            countdownDataStorage?.add(_countdownData.value)

            launch(Dispatchers.Main) {
                _countdownData.update {
                    CountdownData()
                }
                hideSaveWorkoutAlert()
            }
        }
    }
}