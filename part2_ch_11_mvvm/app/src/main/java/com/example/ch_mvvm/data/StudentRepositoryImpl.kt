package com.example.ch_mvvm.data

import com.example.ch_mvvm.domain.Student

class StudentRepositoryImpl : StudentRepositoryInterface {
    override fun getStudent(name: String, major: String, grade: String): Student? {
        return Student(name, major, grade)
    }
}