package com.coolnexttech.freetimer.service

import android.annotation.SuppressLint
import android.content.Context
import com.coolnexttech.freetimer.model.WorkoutData
import com.google.gson.Gson

class StorageService(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FreeTimer", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val gson = Gson()
    private val tempWorkoutDataKey = "temp_workout_data"

    @SuppressLint("ApplySharedPref")
    fun saveTempWorkoutData(workoutData: WorkoutData) {
        val json = gson.toJson(workoutData)
        editor.putString(tempWorkoutDataKey, json)
        editor.commit()
    }

    fun readTempWorkoutData(): WorkoutData? {
        val json = sharedPreferences.getString(tempWorkoutDataKey, null) ?: return null
        return Gson().fromJson(json, WorkoutData::class.java)
    }

    fun removeTempWorkoutData() {
        editor.clear().apply()
    }
}