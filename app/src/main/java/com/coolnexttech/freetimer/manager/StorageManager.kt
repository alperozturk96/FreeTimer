package com.coolnexttech.freetimer.manager

import android.annotation.SuppressLint
import android.content.Context
import com.coolnexttech.freetimer.model.WorkoutData
import com.google.gson.Gson

class StorageManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FreeTimer", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val gson = Gson()
    private val tempWorkoutDataKey = "temp_workout_data"
    private val whenAppInBackgroundKey = "when_app_in_background"

    @SuppressLint("ApplySharedPref")
    fun saveTempWorkoutData(workoutData: WorkoutData) {
        val json = gson.toJson(workoutData)
        editor.putString(tempWorkoutDataKey, json).commit()
    }

    fun readTempWorkoutData(): WorkoutData? {
        val json = sharedPreferences.getString(tempWorkoutDataKey, null) ?: return null
        return Gson().fromJson(json, WorkoutData::class.java)
    }

    fun saveWhenAppInBackground(time: Long) {
        editor.putLong(whenAppInBackgroundKey, time).commit()
    }

    fun readWhenAppInForeground(): Long {
        return sharedPreferences.getLong(whenAppInBackgroundKey, System.currentTimeMillis())
    }

    fun removeTempData() {
        editor.clear().apply()
    }
}