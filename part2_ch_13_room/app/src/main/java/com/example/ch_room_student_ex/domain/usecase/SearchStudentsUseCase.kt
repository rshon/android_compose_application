package com.example.ch_room_student_ex.domain.usecase

import com.example.ch_room_student_ex.domain.model.Student
import com.example.ch_room_student_ex.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    operator fun invoke(query: String): Flow<List<Student>> {
        return repository.searchStudents(query)
    }
}