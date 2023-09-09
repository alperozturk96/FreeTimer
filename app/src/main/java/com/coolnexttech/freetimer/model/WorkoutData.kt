package com.coolnexttech.freetimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkoutData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "set_count") var setCount: Int = 0,
    @ColumnInfo(name = "work_duration") var workDuration: Int = 0,
    @ColumnInfo(name = "rest_duration") var restDuration: Int = 0,
) {
    fun isValid(): Boolean {
        return setCount > 0 && workDuration > 0 && restDuration > 0
    }

    fun isCurrentSetWorkoutFinished(): Boolean {
        return workDuration == 0
    }

    fun isCurrentSetRestFinished(): Boolean {
        return restDuration == 0
    }

    fun isWorkoutFinished(): Boolean {
        return setCount == 0
    }
}
