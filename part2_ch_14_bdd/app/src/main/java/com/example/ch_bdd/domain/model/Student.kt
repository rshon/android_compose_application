package com.example.ch_bdd.domain.model

data class Student(
    val name: String,
    val year: Int, // 학년은 Int로 예상됩니다.
    val department: String, // 학과는 String으로 예상됩니다.
    val id: String // 학번은 String으로 예상됩니다.
)
