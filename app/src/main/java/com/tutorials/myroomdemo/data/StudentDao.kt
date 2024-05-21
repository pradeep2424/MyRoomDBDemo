package com.tutorials.myroomdemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Update
    suspend fun deleteStudent(student: StudentEntity)

    @Query("SELECT * FROM 'student_table'")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM 'student_table' WHERE student_id = :id")
    fun getStudentById(id: Int): Flow<StudentEntity>

}