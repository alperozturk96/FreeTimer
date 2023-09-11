package com.coolnexttech.freetimer.manager

import android.annotation.SuppressLint
import android.content.Context
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.toJson
import com.coolnexttech.freetimer.model.toWorkoutData

class StorageManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FreeTimer", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val tempWorkoutDataKey = "temp_countdown_data"
    private val whenAppInBackgroundKey = "when_app_in_background"

    @SuppressLint("ApplySharedPref")
    fun saveTempWorkoutData(countdownData: CountdownData) {
        editor.putString(tempWorkoutDataKey, countdownData.toJson()).commit()
    }

    fun readTempCountdownData(): CountdownData? {
        val json = sharedPreferences.getString(tempWorkoutDataKey, null) ?: return null
        return json.toWorkoutData()
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