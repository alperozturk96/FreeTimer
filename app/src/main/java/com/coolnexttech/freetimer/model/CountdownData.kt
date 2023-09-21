package com.coolnexttech.freetimer.model

import android.content.Context
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
        println("Countdown Name: $name")
        println("Countdown RestMode : $isRestModeActive")
        println("Countdown Set: $setCount")
        println("Countdown WorkDuration: $workDuration")
        println("Countdown RestDuration: $restDuration")
    }

    fun getTimeLeftInfo(context: Context): String {
        return if (isRestModeActive) {
            context.getString(R.string.count_down_screen_rest_duration_info_text) + restDuration
        } else {
            context.getString(R.string.count_down_screen_work_duration_info_text) + workDuration
        }
    }

    fun getSetCountInfo(context: Context): String {
        return context.getString(R.string.count_down_screen_set_count_info_text) + setCount
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

fun CountdownData.startCountDown(
    mediaPlayerManager: MediaPlayerManager,
    initialWorkoutDuration: Int,
    initialRestDuration: Int
): CountdownData {
    val updatedVal = this.copy()

    updatedVal.print()

    if (updatedVal.isRestModeActive) {
        updatedVal.apply {
            restDuration--
        }

        if (updatedVal.isCurrentSetRestFinished()) {
            updatedVal.apply {
                isRestModeActive = false
                setCount--
                workDuration = initialWorkoutDuration
                restDuration = initialRestDuration
            }

            mediaPlayerManager.playAudio(R.raw.boxing_bell)
        }

        if (updatedVal.isWorkoutFinished()) {
            mediaPlayerManager.stopAudio()
        }
    } else {
        updatedVal.apply {
            workDuration--
        }

        if (updatedVal.isCurrentSetWorkoutFinished()) {
            mediaPlayerManager.playAudio(R.raw.boxing_bell)

            updatedVal.apply {
                isRestModeActive = true
            }
        }
    }

    return updatedVal
}

fun String.toCountdownData(): CountdownData {
    return Gson().fromJson(this, CountdownData::class.java)
}
