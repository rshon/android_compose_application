package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // Producer
    val numberFlow = flow {
        for (i in 1..5) {
            delay(100)
            println("producer: emitting value=$i")
            emit(i)
        }
    }

    // Intermediary
    val transformedFlow = numberFlow
        .filter { it % 2 == 0 }
        .map { it * it }

    // Consumer
    transformedFlow.collect { value ->
        println("consumer: collected value=$value")
    }
}
