package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.manager.StorageManager
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.service.MusicPlayerService
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

    private val _countdownData = MutableStateFlow(CountdownData())
    val countdownData = _countdownData.asStateFlow()
    // endregion

    // region Dependencies
    private var storageManager: StorageManager? = null
    private var mediaPlayerManager: MediaPlayerManager? = null
    private var _initialWorkoutDuration = 0
    private var _initialRestDuration = 0
    // endregion

    fun init(countdownData: CountdownData, context: Context) {
        _countdownData.value = countdownData
        _initialWorkoutDuration = countdownData.workDuration
        _initialRestDuration = countdownData.restDuration
        mediaPlayerManager = MediaPlayerManager(context)
        storageManager = StorageManager(context)
        MusicPlayerService.canStartService = true

        startCountDown()
    }

    private fun startCountDown() {
        viewModelScope.launch(Dispatchers.Main) {
            while (!_isCountdownCompleted.value) {
                _countdownData.value.print()
                handleCountdownData()
                delay(1000)
            }
        }
    }

    // TODO Use Single Source of Truth
    private fun handleCountdownData() {
        if (_countdownData.value.isRestModeActive) {
            handleRestMode()
        } else {
            handleWorkoutMode()
        }
    }

    fun disableMediaPlayer() {
        mediaPlayerManager?.canPlay = false
    }

    private fun releaseMediaPlayer() {
        disableMediaPlayer()
        mediaPlayerManager = null
    }

    fun finishCountDown() {
        _isCountdownCompleted.update {
            true
        }
        releaseMediaPlayer()
        clearTempData()
    }

    // region Handle Lifecycle Changes & Update Workout Data
    fun saveTempCountdownData() {
        storageManager?.saveTempCountdownData(_countdownData.value)
        storageManager?.saveWhenAppInBackground(System.currentTimeMillis())
        println("Temp Countdown Data Saved")
    }

    fun updateCountdownDataWithTempCountdownData() {
        val tempCountdownData = storageManager?.readTempCountdownData() ?: return
        val whenAppInForeground = storageManager?.readWhenAppInForeground() ?: return
        val timeDiffInMilliSecond = System.currentTimeMillis() - whenAppInForeground
        val timeDiffInSecond = (timeDiffInMilliSecond / 1000L).toInt()

        println("Time Difference In Second: $timeDiffInSecond")
        _countdownData.update {
            tempCountdownData
        }

        repeat(timeDiffInSecond) {
            handleCountdownData()
        }

        mediaPlayerManager?.canPlay = true
        println("Countdown Data updated with Temp Countdown Data")
    }

    private fun clearTempData() {
        storageManager?.clear()
    }
    // endregion

    // region Rest Mode
    private fun handleRestMode() {
        _countdownData.update {
            it.copy(restDuration = _countdownData.value.restDuration - 1)
        }

        if (_countdownData.value.isCurrentSetRestFinished()) {
            startNextSet()
        }
    }

    private fun startNextSet() {
        _countdownData.update {
            it.copy(
                isRestModeActive = false,
                setCount = _countdownData.value.setCount - 1,
                workDuration = _initialWorkoutDuration,
                restDuration = _initialRestDuration
            )
        }
        mediaPlayerManager?.playAudio(R.raw.boxing_bell)

        if (_countdownData.value.isWorkoutFinished()) {
            finishCountDown()
        }
    }
    // endregion

    // region Workout Mode
    private fun handleWorkoutMode() {
        _countdownData.update {
            it.copy(workDuration = _countdownData.value.workDuration - 1)
        }

        if (_countdownData.value.isCurrentSetWorkoutFinished()) {
            switchToRestMode()
        }
    }

    private fun switchToRestMode() {
        mediaPlayerManager?.playAudio(R.raw.boxing_bell)
        _countdownData.update {
            it.copy(isRestModeActive = true)
        }
    }
    // endregion
}