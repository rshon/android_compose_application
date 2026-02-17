package com.example.ch_room_student_ex.domain.repository

import com.example.ch_room_student_ex.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    suspend fun addStudent(student: Student)
    suspend fun updateStudent(student: Student)
    suspend fun deleteStudent(student: Student)
    fun getAllStudents(): Flow<List<Student>>
    fun searchStudents(query: String): Flow<List<Student>>
}