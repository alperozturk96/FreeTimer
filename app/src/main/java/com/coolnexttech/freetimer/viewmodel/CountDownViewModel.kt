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

class CountDownViewModel: ViewModel() {
    private val _isTrainingCompleted = MutableStateFlow(false)
    val isTrainingCompleted = _isTrainingCompleted.asStateFlow()

    private val _isRestModeActive = MutableStateFlow(false)
    val isRestModeActive = _isRestModeActive.asStateFlow()

    private val _workoutData = MutableStateFlow(WorkoutData())
    val workoutData = _workoutData.asStateFlow()

    private var musicPlayer: MusicPlayer? = null
    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0

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
                    _workoutData.update {
                        it.copy(restDuration = _workoutData.value.restDuration - 1)
                    }

                    if (_workoutData.value.restDuration == 0) {
                        _workoutData.update {
                            it.copy(setCount = _workoutData.value.setCount - 1)
                        }

                        _isRestModeActive.update {
                            false
                        }
                        _workoutData.update {
                            it.copy(workDuration = _initialWorkoutDuration)
                        }
                        _workoutData.update {
                            it.copy(restDuration = _initialRestDuration)
                        }

                        musicPlayer?.playAudio(R.raw.boxing_bell)

                        if (_workoutData.value.setCount == 0) {
                            musicPlayer?.stopAudio()
                            _isTrainingCompleted.update {
                                true
                            }
                        }
                    }
                } else {
                    _workoutData.update {
                        it.copy(workDuration = _workoutData.value.workDuration - 1)
                    }

                    if (_workoutData.value.workDuration == 0) {
                        musicPlayer?.playAudio(R.raw.boxing_bell)
                        _isRestModeActive.update {
                            true
                        }
                    }
                }

                delay(1000)
            }
        }
    }
}