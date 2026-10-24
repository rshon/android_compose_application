package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun testMap() = runBlocking {
    flowOf(1, 2, 3)
        .map { it * 2 }
        .collect { println(it) }
}

fun testFilter() = runBlocking {
    flowOf(1, 2, 3, 4)
        .filter { it % 2 == 0 } // 짝수만 필터링
        .collect { println(it) }
}

fun testTransform() = runBlocking {
    flowOf(1, 2)
        .transform { value ->
            emit(value)
            emit(value * 10)
        }
        .collect { println(it) }
}

fun testFlatMapConcat() = runBlocking {
    flowOf(1, 2, 3)
        .flatMapConcat { value ->
            flow {
                emit("$value: A")
                delay(500)
                emit("$value: B")
            }
        }
        .collect { println(it) }
}

fun testZip() = runBlocking {
    val numbers = flowOf(1, 2, 3)
    val letters = flowOf("A", "B", "C")
    numbers.zip(letters) { num, letter -> "$num$letter" }
        .collect { println(it) }
}

fun main() {
//    testMap()
//    testFilter()
//    testTransform()

    testFlatMapConcat()
//    testZip()
}
