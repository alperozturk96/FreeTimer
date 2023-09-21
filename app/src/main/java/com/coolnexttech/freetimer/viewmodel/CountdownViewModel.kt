package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.model.CountdownController
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.startCountDown
import com.coolnexttech.freetimer.notification.CountdownNotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class CountdownViewModel: ViewModel() {
    private var _countdownData = MutableStateFlow(CountdownData())
    val countdownData: StateFlow<CountdownData> = _countdownData

    private var _dimScreen = MutableStateFlow(false)
    val dimScreen: StateFlow<Boolean> = _dimScreen

    private var initialWorkoutDuration = 0
    private var initialRestDuration = 0

    private var updateNotificationForPauseMode = true

    private var countdownNotificationService: CountdownNotificationService? = null

    fun init(context: Context) {
        val mediaPlayerManager = MediaPlayerManager(context)
        startCountDown(mediaPlayerManager, context)
        countdownNotificationService = CountdownNotificationService(context)
    }

    fun setupCountdownData(data: CountdownData) {
        initialWorkoutDuration = data.workDuration
        initialRestDuration = data.restDuration

        _countdownData.update {
            data
        }
    }

    private fun startCountDown(mediaPlayerManager: MediaPlayerManager, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            while (!_countdownData.value.isWorkoutFinished()) {
                if (!CountdownController.data.value.resume) {
                    yield()

                    if (updateNotificationForPauseMode) {
                        updateNotification(context)
                        updateNotificationForPauseMode = false
                    }
                    continue
                }

                println("CountDown Started Running")

                _countdownData.update {
                    it.startCountDown(
                        mediaPlayerManager,
                        initialWorkoutDuration,
                        initialRestDuration
                    )
                }

                if (!updateNotificationForPauseMode) {
                    updateNotificationForPauseMode = true
                }

                updateNotification(context)

                delay(1000)
            }

            println("Job is done")
        }
    }

    fun dimScreen(value: Boolean) {
        _dimScreen.update {
            value
        }
    }

    private fun updateNotification(context: Context) {
        val setCountInfo = _countdownData.value.getSetCountInfo(context)
        val timeLeftInfo = _countdownData.value.getTimeLeftInfo(context)
        val notificationIconId = if (_dimScreen.value) {
            R.drawable.ic_circle
        } else {
            R.drawable.ic_timer
        }
        countdownNotificationService?.showNotification(setCountInfo, timeLeftInfo, notificationIconId)
    }

    override fun onCleared() {
        super.onCleared()
        countdownNotificationService?.cancelNotification()
    }
}