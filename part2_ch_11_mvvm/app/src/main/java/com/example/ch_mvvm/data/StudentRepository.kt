package com.example.ch_mvvm.data

import com.example.ch_mvvm.domain.Student

enum class RepositoryKey {
    STUDENT_IMPL
}

class StudentRepository {
    fun getStudent(name: String, major: String, grade: String): Student {
        return Student(name, major, grade) // just create
    }
}

object RepositoryRegistry {
    val factories = mutableMapOf<Any, () -> Any>() // 키 타입을 Any로 변경

    inline fun <reified T : Any> registerFactory(key: Any, noinline factory: () -> T) {
        factories[key] = factory
    }

    inline fun <reified T> create(key: Any): T { // create 함수에 key 파라미터 추가
        val factory = factories[key]
            ?: throw IllegalArgumentException("No factory registered for key: $key of type ${T::class.java.simpleName}")
        return factory() as T
    }
}
