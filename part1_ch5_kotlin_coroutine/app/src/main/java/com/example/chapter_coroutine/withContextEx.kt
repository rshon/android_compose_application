package com.example.chapter_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun testWithContext() = runBlocking {
    println("Start on thread: ${Thread.currentThread().name}")

    val result = withContext(Dispatchers.Default) {
        println("Running CPU task on: ${Thread.currentThread().name}")
        cpuBoundWork()
    }

    println("Back on thread: ${Thread.currentThread().name}")
    println("result: $result")
}

fun cpuBoundWork(): Long {
    var sum = 0L
    for (i in 1..100_000_000) {
        sum += i
    }
    return sum
}
