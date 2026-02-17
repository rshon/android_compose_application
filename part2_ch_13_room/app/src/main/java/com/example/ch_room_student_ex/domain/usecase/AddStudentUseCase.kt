package com.example.ch_room_student_ex.domain.usecase

import com.example.ch_room_student_ex.domain.model.Student
import com.example.ch_room_student_ex.domain.repository.StudentRepository
import javax.inject.Inject

class AddStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(student: Student) {
        repository.addStudent(student)
    }
}