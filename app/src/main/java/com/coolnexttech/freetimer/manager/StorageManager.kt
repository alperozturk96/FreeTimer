package com.coolnexttech.freetimer.manager

import android.annotation.SuppressLint
import android.content.Context
import com.coolnexttech.freetimer.model.CountdownData
import com.coolnexttech.freetimer.model.toJson
import com.coolnexttech.freetimer.model.toCountdownData

class StorageManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FreeTimer", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val tempCountdownDataKey = "temp_countdown_data"
    private val whenAppInBackgroundKey = "when_app_in_background"

    @SuppressLint("ApplySharedPref")
    fun saveTempCountdownData(countdownData: CountdownData) {
        editor.putString(tempCountdownDataKey, countdownData.toJson()).commit()
    }

    fun readTempCountdownData(): CountdownData? {
        val json = sharedPreferences.getString(tempCountdownDataKey, null) ?: return null
        return json.toCountdownData()
    }

    fun saveWhenAppInBackground(time: Long) {
        editor.putLong(whenAppInBackgroundKey, time).commit()
    }

    fun readWhenAppInForeground(): Long {
        return sharedPreferences.getLong(whenAppInBackgroundKey, System.currentTimeMillis())
    }

    fun clear() {
        editor.clear().apply()
    }
}