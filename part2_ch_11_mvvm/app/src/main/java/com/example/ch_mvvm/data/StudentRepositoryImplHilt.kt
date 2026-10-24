package com.example.ch_mvvm.data

import com.example.ch_mvvm.domain.Student
import javax.inject.Inject

class StudentRepositoryImplHilt @Inject constructor() : StudentRepositoryInterface {
    override fun getStudent(name: String, major: String, grade: String): Student? {
        return Student(name, major, grade)
    }
}
