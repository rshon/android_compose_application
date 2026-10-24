package com.example.ch_test.domain.repository

import com.example.ch_test.domain.model.Student

interface StudentRepository {
    suspend fun getStudent(studentId: String): Result<Student>
}
