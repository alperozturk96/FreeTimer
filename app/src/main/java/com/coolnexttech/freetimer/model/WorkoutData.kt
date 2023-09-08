package com.coolnexttech.freetimer.model

data class WorkoutData(
    var setCount: Int = 0,
    var workDuration: Int = 0,
    var restDuration: Int = 0,
) {
    fun isValid(): Boolean {
        return setCount > 0 && workDuration > 0 && restDuration > 0
    }
}
