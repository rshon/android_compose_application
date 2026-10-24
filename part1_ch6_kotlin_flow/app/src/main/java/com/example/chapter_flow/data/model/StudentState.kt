package com.example.chapter_flow.data.model

data class StudentState(
    val student: Student?,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
