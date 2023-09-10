package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
import com.coolnexttech.freetimer.manager.StorageManager
import com.coolnexttech.freetimer.util.MusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel() {

    // region Flows
    private val _isCountdownCompleted = MutableStateFlow(false)
    val isCountDownCompleted = _isCountdownCompleted.asStateFlow()

    private val _workoutData = MutableStateFlow(WorkoutData())
    val workoutData = _workoutData.asStateFlow()
    // endregion

    // region Dependencies
    private var storageManager: StorageManager? = null
    private var musicPlayer: MusicPlayer? = null
    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0
    // endregion

    fun init(workoutData: WorkoutData, context: Context) {
        _workoutData.value = workoutData
        _initialWorkoutDuration = workoutData.workDuration
        _initialRestDuration = workoutData.restDuration
        musicPlayer = MusicPlayer(context)
        storageManager = StorageManager(context)

        startCountDown()
    }

    private fun startCountDown() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                handleWorkoutData()
                delay(1000)
            }
        }
    }

    private fun handleWorkoutData() {
        if (_workoutData.value.isRestModeActive) {
            handleRestMode()
        } else {
            handleWorkoutMode()
        }
    }

    // region Handle Lifecycle Changes & Update Workout Data
    fun saveTempWorkoutData() {
        storageManager?.saveTempWorkoutData(_workoutData.value)
        storageManager?.saveWhenAppInBackground(System.currentTimeMillis())
        println("Temp Workout Data Saved")
    }

    fun updateWorkoutDataWithTempWorkoutData() {
        val tempWorkoutData = storageManager?.readTempWorkoutData() ?: return
        val whenAppInForeground = storageManager?.readWhenAppInForeground() ?: return
        val timeDiffInMilliSecond = System.currentTimeMillis() - whenAppInForeground
        val timeDiffInSecond = (timeDiffInMilliSecond / 1000L).toInt()
        musicPlayer?.canPlay = false

        println("Time Difference In Second: $timeDiffInSecond")
        _workoutData.update {
            tempWorkoutData
        }

        repeat(timeDiffInSecond) {
            handleWorkoutData()
        }

        musicPlayer?.canPlay = true
        println("Workout Data updated with Temp Workout Data")
    }

    fun removeTempWorkoutData() {
        storageManager?.removeTempData()
    }
    // endregion

    // region Rest Mode
    private fun handleRestMode() {
        _workoutData.update {
            it.copy(restDuration = _workoutData.value.restDuration - 1)
        }

        if (_workoutData.value.isCurrentSetRestFinished()) {
            startNextSet()
        }
    }

    private fun startNextSet() {
        _workoutData.update {
            it.copy(
                isRestModeActive = false,
                setCount = _workoutData.value.setCount - 1,
                workDuration = _initialWorkoutDuration,
                restDuration = _initialRestDuration
            )
        }
        musicPlayer?.playAudio(R.raw.boxing_bell)

        if (_workoutData.value.isWorkoutFinished()) {
            stopCountdown()
        }
    }

    private fun stopCountdown() {
        musicPlayer?.stopAudio()
        _isCountdownCompleted.update {
            true
        }
    }
    // endregion

    // region Workout Mode
    private fun handleWorkoutMode() {
        _workoutData.update {
            it.copy(workDuration = _workoutData.value.workDuration - 1)
        }

        if (_workoutData.value.isCurrentSetWorkoutFinished()) {
            switchToRestMode()
        }
    }

    private fun switchToRestMode() {
        musicPlayer?.playAudio(R.raw.boxing_bell)
        _workoutData.update {
            it.copy(isRestModeActive = true)
        }
    }
    // endregion
}