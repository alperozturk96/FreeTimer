package com.coolnexttech.freetimer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.coolnexttech.freetimer.model.CountdownData

@Database(entities = [CountdownData::class], version = 1)
abstract class CountdownDataDb : RoomDatabase() {
    abstract fun countdownDao(): CountdownDao
}