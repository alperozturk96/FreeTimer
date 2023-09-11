package com.coolnexttech.freetimer.db

import android.content.Context
import androidx.room.Room
import com.coolnexttech.freetimer.model.CountdownData
import java.util.UUID

class CountdownDataStorage private constructor(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        CountdownDataDb::class.java, "free-timer-db"
    ).build().countdownDao()

    companion object {
        @Volatile
        private var instance: CountdownDataStorage? = null

        fun getInstance(context: Context): CountdownDataStorage {
            return instance ?: synchronized(this) {
                instance ?: CountdownDataStorage(context).also { instance = it }
            }
        }
    }

    fun delete(countdownData: CountdownData) {
        db.delete(countdownData)
    }

    fun add(countdownData: CountdownData) {
        db.insert(countdownData)
    }

    fun findById(id: UUID): CountdownData {
        return db.findById(id)
    }

    fun getAll(): List<CountdownData> {
        return db.getAll()
    }
}