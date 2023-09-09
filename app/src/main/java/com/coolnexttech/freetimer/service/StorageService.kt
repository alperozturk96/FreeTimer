package com.coolnexttech.freetimer.service

import android.content.Context
import com.coolnexttech.freetimer.model.WorkoutData

class StorageService(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FreeTimer", Context.MODE_PRIVATE)
}