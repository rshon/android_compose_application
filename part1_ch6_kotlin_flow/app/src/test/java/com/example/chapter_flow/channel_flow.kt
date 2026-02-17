package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    val studentFlow: Flow<Student> = channelFlow {
        launch {
            println("starts 1st coroutine - ${Thread.currentThread().name}")
            send(Student("Foo", "Computer Science", 85))
            delay(100)
            send(Student("Bar", "Mathematics", 92))
            println("finished 1st coroutine")
        }

        launch {
            println("starts 2nd coroutine - ${Thread.currentThread().name}")
            delay(50)
            send(Student("Boo", "Electrical Engineering", 78))
            delay(100)
            send(Student("Baz", "Electronics", 95))
            println("finished 2nd coroutine")
        }
    }

    println("starts to collect")
    studentFlow.collect { student ->
        println("Collecting: ${student.name} (ID: ${student.major}) - ${student.grade}")
    }
}