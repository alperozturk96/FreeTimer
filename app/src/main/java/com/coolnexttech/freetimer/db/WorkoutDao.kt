package com.coolnexttech.freetimer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.coolnexttech.freetimer.model.WorkoutData
import java.util.UUID

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workoutdata")
    fun getAll(): List<WorkoutData>

    @Query("SELECT * FROM workoutdata WHERE id IN (:id)")
    fun findById(id: UUID): WorkoutData

    @Insert
    fun insert(vararg workoutDataList: WorkoutData)

    @Delete
    fun delete(workoutData: WorkoutData)
}