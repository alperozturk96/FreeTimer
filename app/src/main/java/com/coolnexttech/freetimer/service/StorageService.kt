package com.coolnexttech.freetimer.service

import android.content.Context
import androidx.room.Room

class StorageService(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        WorkoutDatabase::class.java, "free-timer-db"
    ).build()


}