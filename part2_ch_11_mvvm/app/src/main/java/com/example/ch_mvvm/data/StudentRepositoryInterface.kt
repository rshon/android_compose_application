package com.example.ch_mvvm.data

import com.example.ch_mvvm.domain.Student

interface StudentRepositoryInterface {
    fun getStudent(name: String, major: String, grade: String): Student?
}