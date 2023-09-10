package com.coolnexttech.freetimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import java.util.UUID

@Entity
data class WorkoutData(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "is_rest_mode_active") var isRestModeActive: Boolean = false,
    @ColumnInfo(name = "set_count") var setCount: Int = 0,
    @ColumnInfo(name = "work_duration") var workDuration: Int = 0,
    @ColumnInfo(name = "rest_duration") var restDuration: Int = 0,
) {
    fun print() {
        println("Workout Name: $name")
        println("Workout RestMode : $isRestModeActive")
        println("Workout Set: $setCount")
        println("Workout WorkDuration: $workDuration")
        println("Workout RestDuration: $restDuration")
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

fun WorkoutData.toJson(): String {
    return Gson().toJson(this)
}

fun String.toWorkoutData(): WorkoutData {
    return Gson().fromJson(this, WorkoutData::class.java)
}