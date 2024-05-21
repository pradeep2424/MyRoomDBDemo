package com.tutorials.myroomdemo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_table")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "student_id")
    val id: Int = 0,
    @ColumnInfo(name = "student_name")
    val name: String = "",
    @ColumnInfo(name = "student_email")
    val email: String = ""
)
