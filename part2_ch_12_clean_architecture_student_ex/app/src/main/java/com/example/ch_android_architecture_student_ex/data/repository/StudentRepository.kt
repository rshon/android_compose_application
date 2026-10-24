package com.example.ch_android_architecture_student_ex.data.repository

import com.example.ch_android_architecture_student_ex.data.model.Student

interface StudentRepository {
    suspend fun getStudents(): List<Student>

    suspend fun add(student: Student)
}
