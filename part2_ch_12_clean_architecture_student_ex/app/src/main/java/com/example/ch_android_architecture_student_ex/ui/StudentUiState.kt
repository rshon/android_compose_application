package com.example.ch_android_architecture_student_ex.ui

import com.example.ch_android_architecture_student_ex.data.model.Student

data class StudentUiState(
    val isLoading: Boolean = false,
    val students: List<Student> = emptyList(),
    val error: String? = null
)
