package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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

    private var _isCountdownStarted = MutableStateFlow(false)
    val isCountdownStarted: StateFlow<Boolean> = _isCountdownStarted

    private var _dimScreen = MutableStateFlow(false)
    val dimScreen: StateFlow<Boolean> = _dimScreen

    private var updateNotificationForPauseMode = true

    private var countdownNotificationService: CountdownNotificationService? = null

    fun init(context: Context, navController: NavHostController) {
        val mediaPlayerManager = MediaPlayerManager(context)
        startCountDown(mediaPlayerManager, context, navController)
        countdownNotificationService = CountdownNotificationService(context)
    }

    fun setupCountdownData(data: CountdownData) {
        _countdownData.update {
            it.setInitialDurations(it)
            data
        }
    }

    private fun startCountDown(mediaPlayerManager: MediaPlayerManager, context: Context, navController: NavHostController) {
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
                    it.startCountDown(mediaPlayerManager)
                }

                if (!updateNotificationForPauseMode) {
                    updateNotificationForPauseMode = true
                }

                updateNotification(context)

                delay(1000)

                if (!_isCountdownStarted.value) {
                    _isCountdownStarted.update {
                        true
                    }
                }
            }

            println("Job is done")

            finishCountdown(navController, mediaPlayerManager)
        }
    }

    fun dimScreen(value: Boolean) {
        _dimScreen.update {
            value
        }
    }

    private fun finishCountdown(navController: NavHostController, mediaPlayerManager: MediaPlayerManager) {
        navController.popBackStack()
        mediaPlayerManager.playAudio(R.raw.finish_boxing_bell)
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayerManager.stopAudio()
        }, 5000)
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