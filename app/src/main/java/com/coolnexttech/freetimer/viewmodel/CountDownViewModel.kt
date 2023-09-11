package com.coolnexttech.freetimer.viewmodel

import androidx.lifecycle.ViewModel
import com.coolnexttech.freetimer.model.CountdownData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CountDownViewModel : ViewModel() {

    // region Flows
    companion object {
        var countdownData = MutableStateFlow(CountdownData())
        var initialWorkoutDuration = 0
        var initialRestDuration = 0

        fun setupCountdownData(data: CountdownData) {
            initialWorkoutDuration = data.workDuration
            initialRestDuration = data.restDuration

            countdownData.update {
                data
            }
        }
    }
    // endregion

    fun reset() {
        countdownData = MutableStateFlow(CountdownData())
        initialWorkoutDuration = 0
        initialRestDuration = 0
    }
}