package com.example.lab08

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :query || '%' ORDER BY description ASC")
    fun searchSortedByName(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :query || '%' ORDER BY created_date DESC")
    fun searchSortedByDate(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :query || '%' ORDER BY is_completed ASC")
    fun searchSortedByStatus(query: String): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: Int)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
