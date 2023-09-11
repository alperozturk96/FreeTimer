package com.coolnexttech.freetimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolnexttech.freetimer.R
import com.coolnexttech.freetimer.manager.MediaPlayerManager
import com.google.gson.Gson
import java.util.UUID

@Entity
data class CountdownData(
    @PrimaryKey var id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "is_rest_mode_active") var isRestModeActive: Boolean = false,
    @ColumnInfo(name = "set_count") var setCount: Int = 0,
    @ColumnInfo(name = "work_duration") var workDuration: Int = 0,
    @ColumnInfo(name = "rest_duration") var restDuration: Int = 0,
) {
    fun print() {
        println("------------------------------------")
        println("Workout Name: $name")
        println("Workout RestMode : $isRestModeActive")
        println("Workout Set: $setCount")
        println("Workout WorkDuration: $workDuration")
        println("Workout RestDuration: $restDuration")
    }

    // TODO FIX
    fun startCountDown(mediaPlayerManager: MediaPlayerManager, initialWorkoutDuration: Int, initialRestDuration: Int): CountdownData {
        if (isRestModeActive) {
            restDuration -= 1

            if (isCurrentSetRestFinished()) {
                isRestModeActive = false
                setCount -= 1
                workDuration = initialWorkoutDuration
                restDuration = initialRestDuration
            }
            mediaPlayerManager.playAudio(R.raw.boxing_bell)
            if (isWorkoutFinished()) {
                mediaPlayerManager.stopAudio()
            }
        } else {
            workDuration -= 1
            if (isCurrentSetWorkoutFinished()) {
                mediaPlayerManager.playAudio(R.raw.boxing_bell)
                isRestModeActive = true
            }
        }

        return this
    }

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

fun CountdownData.toJson(): String {
    return Gson().toJson(this)
}

fun String.toWorkoutData(): CountdownData {
    return Gson().fromJson(this, CountdownData::class.java)
}