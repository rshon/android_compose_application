package com.example.ch_android_architecture_student_ex.domain

import com.example.ch_android_architecture_student_ex.data.model.Student
import com.example.ch_android_architecture_student_ex.data.repository.StudentRepository

class GetStudentsUseCase(
    private val repo: StudentRepository
) {
    suspend operator fun invoke(): List<Student> {
        val students = repo.getStudents()
        if (students.isEmpty()) {
            // dummy
            repo.add(Student(id = 2, name = "Foo", grade = 3.5))
            repo.add(Student(id = 3, name = "Bar", grade = 3.0))

            return repo.getStudents()
        }
        return students
    }
}