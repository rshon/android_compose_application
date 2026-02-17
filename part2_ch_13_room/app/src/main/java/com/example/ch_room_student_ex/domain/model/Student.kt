package com.example.ch_room_student_ex.domain.model

data class Student(
    val id: Int = 0,
    val name: String,
    val studentId: String,
    val major: String,
    val grade: Int
)