package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun testFlowEx1() = runBlocking {
    val myFlow = flow {
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }
    myFlow.collect { println(it) }
}

fun testFlowEx2() = runBlocking {
    val myFlow = flowOf(10, 20, 30)
    myFlow.collect { println(it) }
}

fun main() {
//    testFlowEx1()
    testFlowEx2()
}
