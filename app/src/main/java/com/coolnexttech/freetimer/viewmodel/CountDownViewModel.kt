package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.model.WorkoutData
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

    private val _isRestModeActive = MutableStateFlow(false)
    val isRestModeActive = _isRestModeActive.asStateFlow()

    private val _workoutData = MutableStateFlow(WorkoutData())
    val workoutData = _workoutData.asStateFlow()
    // endregion

    // region Dependencies
    private var musicPlayer: MusicPlayer? = null
    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0
    // endregion

    fun init(workoutData: WorkoutData, context: Context) {
        _workoutData.value = workoutData
        _initialWorkoutDuration = workoutData.workDuration
        _initialRestDuration = workoutData.restDuration
        musicPlayer = MusicPlayer(context)

        startCountDown()
    }

    private fun startCountDown() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                if (_isRestModeActive.value) {
                    handleRestMode()
                } else {
                    handleWorkoutMode()
                }

                delay(1000)
            }
        }
    }

    // region Rest Mode
    private fun handleRestMode() {
        _workoutData.update {
            it.copy(restDuration = _workoutData.value.restDuration - 1)
        }

        if (_workoutData.value.restDuration == 0) {
            startNextSet()
        }
    }

    private fun startNextSet() {
        _isRestModeActive.update {
            false
        }
        _workoutData.update {
            it.copy(
                setCount = _workoutData.value.setCount - 1,
                workDuration = _initialWorkoutDuration,
                restDuration = _initialRestDuration
            )
        }
        musicPlayer?.playAudio(R.raw.boxing_bell)

        if (_workoutData.value.setCount == 0) {
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

        if (_workoutData.value.workDuration == 0) {
            switchToRestMode()
        }
    }

    private fun switchToRestMode() {
        musicPlayer?.playAudio(R.raw.boxing_bell)
        _isRestModeActive.update {
            true
        }
    }
    // endregion
}