package com.example.ch_mvvm.data

import com.example.ch_mvvm.domain.Student

import javax.inject.Inject

class StudentRepositoryImplHiltMock @Inject constructor() : StudentRepositoryInterface {
    override fun getStudent(name: String, major: String, grade: String): Student? {
        return Student("Name from Impl 2", "Major from Impl 2", "Grade from Impl 2")
    }
}