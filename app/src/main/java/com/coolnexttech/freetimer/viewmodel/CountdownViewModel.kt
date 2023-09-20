package com.coolnexttech.freetimer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.play
import com.coolnexttech.freetimer.model.startCountDown
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

    private var initialWorkoutDuration = 0
    private var initialRestDuration = 0

    fun init(context: Context) {
        val mediaPlayerManager = MediaPlayerManager(context)
        startCountDown(mediaPlayerManager)

        if (!play.value) {
            play.update {
                true
            }
        }
    }

    fun setupCountdownData(data: CountdownData) {
        initialWorkoutDuration = data.workDuration
        initialRestDuration = data.restDuration

        _countdownData.update {
            data
        }
    }

    private fun startCountDown(mediaPlayerManager: MediaPlayerManager) {
        viewModelScope.launch(Dispatchers.Main) {
            while (!_countdownData.value.isWorkoutFinished()) {
                if (!play.value) {
                    println("CountDown Stopped")
                    yield()
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
                delay(1000)
            }

            println("Job is done")
        }
    }
}