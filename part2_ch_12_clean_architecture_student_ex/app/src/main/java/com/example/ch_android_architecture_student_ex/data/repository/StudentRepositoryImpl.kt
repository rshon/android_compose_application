package com.example.ch_android_architecture_student_ex.data.repository

import com.example.ch_android_architecture_student_ex.data.local.StudentDao
import com.example.ch_android_architecture_student_ex.data.model.Student

class StudentRepositoryImpl(
    private val dao: StudentDao
) : StudentRepository {
    override suspend fun getStudents(): List<Student> = dao.getAllStudents()

    override suspend fun add(student: Student) {
        dao.insert(
            Student(
                id    = student.id,
                name  = student.name,
                grade = student.grade
            )
        )
    }
}