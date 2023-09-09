package com.coolnexttech.freetimer.db

import android.content.Context
import androidx.room.Room
import com.coolnexttech.freetimer.model.WorkoutData

class WorkoutDataStorage private constructor(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        WorkoutDatabase::class.java, "free-timer-db"
    ).build().workoutDao()

    companion object {
        @Volatile
        private var instance: WorkoutDataStorage? = null

        fun getInstance(context: Context): WorkoutDataStorage {
            return instance ?: synchronized(this) {
                instance ?: WorkoutDataStorage(context).also { instance = it }
            }
        }
    }

    fun delete(workoutData: WorkoutData) {
        db.delete(workoutData)
    }

    fun add(workoutData: WorkoutData) {
        db.insert(workoutData)
    }

    fun findById(id: Int): WorkoutData {
        return db.findById(id)
    }

    fun getAll(): List<WorkoutData> {
        return db.getAll()
    }
}