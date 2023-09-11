package com.coolnexttech.freetimer.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

fun resetCountDownData() {
    countdownData = MutableStateFlow(CountdownData())
    initialWorkoutDuration = 0
    initialRestDuration = 0
}