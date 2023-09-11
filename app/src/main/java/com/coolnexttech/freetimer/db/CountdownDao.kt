package com.coolnexttech.freetimer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.coolnexttech.freetimer.model.CountdownData
import java.util.UUID

@Dao
interface CountdownDao {
    @Query("SELECT * FROM countdowndata")
    fun getAll(): List<CountdownData>

    @Query("SELECT * FROM countdowndata WHERE id IN (:id)")
    fun findById(id: UUID): CountdownData

    @Insert
    fun insert(vararg countdownDataList: CountdownData)

    @Delete
    fun delete(countdownData: CountdownData)
}