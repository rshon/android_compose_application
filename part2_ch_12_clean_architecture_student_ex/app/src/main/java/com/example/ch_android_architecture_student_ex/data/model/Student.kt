package com.example.ch_android_architecture_student_ex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Long,
    val name: String,
    val grade: Double
)
