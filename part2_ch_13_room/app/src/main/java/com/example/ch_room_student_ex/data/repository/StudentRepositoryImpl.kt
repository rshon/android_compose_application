package com.example.ch_room_student_ex.data.repository

import com.example.ch_room_student_ex.data.local.dao.StudentDao
import com.example.ch_room_student_ex.data.local.entity.StudentEntity
import com.example.ch_room_student_ex.domain.model.Student
import com.example.ch_room_student_ex.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao
) : StudentRepository {
    override suspend fun addStudent(student: Student) {
        studentDao.insert(student.toEntity())
    }

    override suspend fun updateStudent(student: Student) {
        studentDao.update(student.toEntity())
    }

    override suspend fun deleteStudent(student: Student) {
        studentDao.delete(student.toEntity())
    }

    override fun getAllStudents(): Flow<List<Student>> {
        return studentDao.getAllStudents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchStudents(query: String): Flow<List<Student>> {
        return studentDao.searchStudents(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

fun Student.toEntity() = StudentEntity(
    id = id,
    name = name,
    studentId = studentId,
    major = major,
    grade = grade
)

fun StudentEntity.toDomain() = Student(
    id = id,
    name = name,
    studentId = studentId,
    major = major,
    grade = grade
)