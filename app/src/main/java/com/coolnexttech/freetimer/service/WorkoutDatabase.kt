package com.coolnexttech.freetimer.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coolnexttech.freetimer.model.WorkoutData

@Database(entities = [WorkoutData::class], version = 1)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}