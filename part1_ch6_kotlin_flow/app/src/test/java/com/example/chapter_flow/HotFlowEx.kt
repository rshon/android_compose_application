package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun testHotStateFlow() = runBlocking {
    val stateFlow = MutableStateFlow(1)

    val emitterJob = launch {
        for (i in 2..5) {
            delay(200)
            stateFlow.value = i
            println("StateFlow emitted: $i")
        }
    }

    val collectorJob1 = launch {
        stateFlow.collect { value ->
            println("Collector 1 received: $value")
        }
    }

    delay(400)

    val collectorJob2 = launch {
        stateFlow.collect { value ->
            println("Collector 2 received: $value")
        }
    }

    delay(500)

    collectorJob1.cancel()
    collectorJob2.cancel()
    emitterJob.cancel()
}

fun testHotSharedFlow() = runBlocking {
    val sharedFlow = MutableSharedFlow<String>(replay = 3)

    val producer = launch {
        delay(50)
        sharedFlow.emit("1")
        delay(100)
        sharedFlow.emit("2")
        delay(100)
        sharedFlow.emit("3")
        println("SharedFlow emitted all values")
    }

    val consumer1st = launch {
        sharedFlow.collect { value ->
            println("Collector A received: $value")
        }
    }

    delay(300) // 약간의 딜레이 후 두 번째 수집자 시작

    val consumer2nd = launch {
        sharedFlow.collect { value ->
            println("Collector B received: $value")
        }
    }

    delay(500) // 프로그램 종료 대기

    producer.cancel()
    consumer1st.cancel()
    consumer2nd.cancel()
}

fun main() {
//    testHotStateFlow()
    testHotSharedFlow()
}
