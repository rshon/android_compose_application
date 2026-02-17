package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun testCollect() = runBlocking {
    flowOf("Hello", "World")
        .collect { println(it) }
}

fun testCollectOnEach() = runBlocking {
    flowOf(1, 2, 3)
        .onEach { println("Processing $it") }
        .collect()
}

fun testCollectLatest() = runBlocking {
    flow {
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }
        .collectLatest { value ->
            println("Start processing $value")
            delay(150)
            println("Finished processing $value")
        }
}

fun testHandlingExcpetion() = runBlocking {
    flow {
        emit(1)
        throw RuntimeException("Error!")
    }
        .catch { e -> println("Caught: ${e.message}") }
        .collect { println(it) }
}

fun testHandlingException2() = runBlocking {
    val flow = flow {
        emit(1)
        throw RuntimeException("Error!")
    }.catch { e ->
        println("Caught: ${e.message}")
        emit(-1)
    }

    val result = flow.toList()
    result.forEach { println(it) }
}

fun main() {
//    testCollect()
//    testCollectOnEach()
//    testCollectLatest()
//    testHandlingExcpetion()
    testHandlingException2()
}

