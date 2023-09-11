package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.manager.StorageManager
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.startCountDown
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
                handleCountdownData()
                delay(1000)
            }
        }
    }

    private fun handleCountdownData() {
        _countdownData.update {
            it.startCountDown(mediaPlayerManager!!, _initialWorkoutDuration, _initialRestDuration)
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
}